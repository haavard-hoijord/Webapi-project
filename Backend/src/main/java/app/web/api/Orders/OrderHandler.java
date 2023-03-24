package app.web.api.Orders;

import app.web.api.JavaWebApiApplication;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static app.web.api.Orders.OrderRestHandler.orderValidator;


public class OrderHandler {
	public static List<Order> getAllOrders() {
		List<Order> list = new ArrayList<>();

		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.createQuery("from Order", Order.class).list().forEach(Order -> {
				if (orderValidator.run(Order)) {
					list.add(Order);
				}
			});
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static Order getOrder(Long id) {
		Order Order = null;

		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			Order = session.get(Order.class, id);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Order;
	}

	public static void addOrder(Order order) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.persist(order);
			session.flush();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void putOrder(Order newOrder, Long id) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.merge(newOrder);
			session.flush();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteOrder(Long id) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			Order order = session.get(Order.class, id);
			session.remove(order);
			session.flush();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
