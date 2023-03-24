package app.web.api.Products;

import app.web.api.JavaWebApiApplication;
import app.web.api.Validator;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductHandler {
	private static final Validator<Product> productValidator = Validator.of(
			(product) -> product.getPrice() >= 0,
			(product) -> product.stock > 0,
			(product) -> product.getProductName() != null,
			(product) -> !product.getProductName().isBlank());


	@GetMapping("/products")
	public static List<Product> getAllProducts() {
		List<Product> list = new ArrayList<>();

		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.createQuery("from Product", Product.class).list().forEach(Product -> {
				if (productValidator.run(Product)) {
					list.add(Product);
				}
			});
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}


	public static Product getProduct(Long id) {
		Product product = null;

		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			product = session.get(Product.class, id);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return product;
	}

	public static void addProduct(Product product) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.persist(product);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void putProduct(Product newProduct) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.merge(newProduct);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteProduct(Long id) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			Product product = session.get(Product.class, id);
			session.remove(product);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
