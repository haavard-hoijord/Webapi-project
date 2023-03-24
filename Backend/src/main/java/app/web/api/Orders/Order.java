package app.web.api.Orders;

import app.web.api.Products.Product;
import app.web.api.Users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

//Hibernate ORM
@Entity
@Table(name = "ORDERS")

//Lombok
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	@Id
	@GeneratedValue
	public Long orderId;

	@ManyToOne
	@JsonIgnoreProperties({"orders", "password"})
	public User user;

	@ElementCollection
	@CollectionTable(name = "ORDERS_ITEM_PRICES", joinColumns = {@JoinColumn(name = "orderId", referencedColumnName = "orderId")})
	@MapKeyColumn(name = "productId")
	@Column(name = "price")
	public Map<Long, Double> prices;

	@ElementCollection
	@CollectionTable(name = "ORDERS_ITEMS_COUNTS", joinColumns = {@JoinColumn(name = "orderId", referencedColumnName = "orderId")})
	@MapKeyColumn(name = "productId")
	@Column(name = "count")
	public Map<Long, Long> productCount;

	public Date orderDate;

	@OneToMany(fetch = FetchType.EAGER)
	public List<Product> products;

	public Double getPrice() {
		return prices == null ? 0 : prices.values().stream().reduce(0d, Double::sum);
	}

}
