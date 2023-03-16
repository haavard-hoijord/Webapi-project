package app.web.api.Users;

import app.web.api.Orders.Order;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//Hibernate ORM
@Entity
@Table(name = "USERS")

//Lombok
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue
	Long userId;

	String username;
	String name;

	String password;
	String email;
	String phoneNumber;

	String userImage;

	@JsonIgnoreProperties({"prices"})
	@OneToMany(fetch = FetchType.EAGER)
	List<Order> orders = new ArrayList<>();
}
