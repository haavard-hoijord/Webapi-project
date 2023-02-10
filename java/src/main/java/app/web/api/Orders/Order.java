package app.web.api.Orders;

import app.web.api.Users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Table(name = "ORDERS")
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
    @JsonIgnoreProperties({"orders"})
    public User user;

    public Double price;
    public Date orderDate;
}
