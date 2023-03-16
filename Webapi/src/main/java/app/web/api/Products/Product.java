package app.web.api.Products;

import jakarta.persistence.*;
import lombok.*;

//Hibernate ORM
@Entity
@Table(name = "PRODUCTS")

//Lombok
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue
	public Long productId;

	public String productName;

	public String productDescription;

	public String productImage;

	public Double price;

	public int stock;
}
