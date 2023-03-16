package app.web.api;

import app.web.api.Orders.Order;
import app.web.api.Orders.OrderHandler;
import app.web.api.Products.Product;
import app.web.api.Products.ProductHandler;
import app.web.api.Sessions.SessionHandler;
import app.web.api.Users.User;
import app.web.api.Users.UserHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class StoreHandler {
	public static final Validator<Order> orderValidator = Validator.of(
			(order) -> order.prices.size() > 0 && order.products.size() > 0,
			(order) -> order.prices.size() == order.products.size(),
			(order) -> order.products.stream().allMatch(s -> s.stock > 0),
			(order) -> order.prices.values().stream().allMatch(s -> s >= 0),
			(order) -> order.user != null,
			(order) -> order.orderDate != null
	);

	@PostMapping("/store/purchase")
	public Order purchaseItems(@RequestHeader String token, @RequestHeader Long id, @RequestBody Map<Long, Long> productList) {
		User user = UserHandler.getUser(id);

		if (user != null && SessionHandler.verifySession(id.toString(), token)) {
			Map<Long, Long> productCount = new HashMap<>();
			List<Product> products = new ArrayList<>();
			for (Map.Entry<Long, Long> entry : productList.entrySet()) {
				Product product = ProductHandler.getProduct(entry.getKey());

				if (product != null && product.stock > 0) {
					productCount.put(product.productId, Math.min(entry.getValue(), product.stock));
					products.add(product);
				}
			}

			Map<Long, Double> productPrices = new HashMap<>();

			for (Map.Entry<Long, Long> entry : productCount.entrySet()) {
				productPrices.put(entry.getKey(), getProductPrice(user, products.stream().filter(s -> Objects.equals(s.getProductId(), entry.getKey())).findFirst().get(), entry.getValue()));
			}

			Order order = Order.builder().products(products).productCount(productCount).prices(productPrices).orderDate(new Date()).user(user).build();

			if (orderValidator.run(order)) {
				for (Product product : products) {
					Product temp = ProductHandler.getProduct(product.productId);
					temp.stock -= order.getProductCount().getOrDefault(product.productId, 1L);

					ProductHandler.putProduct(temp);
				}

				OrderHandler.addOrder(order);

				user.getOrders().add(order);
				UserHandler.putUser(user, user.getUserId());

				return order;
			}
		}


		return null;
	}

	private static Double getProductPrice(User user, Product product, Long count) {
		long orderCount = user.getOrders().stream()
				.filter(s -> s.products.stream().anyMatch(s1 -> Objects.equals(s1.productId, product.productId)))
				.filter(s -> System.currentTimeMillis() - s.getOrderDate().getTime() <= TimeUnit.MILLISECONDS.convert(10, TimeUnit.DAYS))
				.map(s -> s.getProductCount().getOrDefault(product.getProductId(), 0L)).reduce(0L, Long::sum) + count;
		return Math.round((product.getPrice() * (1f - Math.min(0.25f, 0.25f * (orderCount / 10f)))) * 100d) / 100d;
	}
}
