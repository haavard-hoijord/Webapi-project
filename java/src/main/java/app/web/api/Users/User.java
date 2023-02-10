package app.web.api.Users;

import app.web.api.Orders.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "USERS")
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

    @OneToMany(fetch = FetchType.EAGER)
    List<Order> orders;
}
