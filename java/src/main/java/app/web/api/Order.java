package app.web.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
public class Order {
    public Long id;
    public Long userID;
    public Double price;
    public Date orderDate;
}
