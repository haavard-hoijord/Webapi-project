package app.web.api.Orders;

import app.web.api.Validator;
import app.web.api.JavaWebApiApplication;
import org.hibernate.Session;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderRestHandler {
    private final OrderModelAssembler assembler;

    private final Validator<Order> orderValidator =
            new Validator<>((order) -> order.orderId != 0,
            new Validator<>((order) -> order.orderDate != null,
            new Validator<>((order) -> order.user != null,
            new Validator<>((order) -> order.price >= 0))));


    OrderRestHandler(OrderModelAssembler assembler) {
        this.assembler = assembler;
    }
    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> getAllOrders(){
        List<EntityModel<Order>> list = new ArrayList<>();

        try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("from Order", Order.class).list().forEach(Order -> {
                if(orderValidator.run(Order)) {
                    list.add(assembler.toModel(Order));
                }
            });
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CollectionModel.of(list, linkTo(methodOn(OrderRestHandler.class).getAllOrders()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> getOrder(@PathVariable Long id){
        Order Order = null;

        try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
            session.beginTransaction();
            Order = session.get(Order.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert Order != null;
        return assembler.toModel(Order);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> addOrder(@RequestBody Order order){
        if(orderValidator.run(order)) {
            try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.persist(order);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            EntityModel<Order> model = assembler.toModel(order);
            return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<?> putOrder(@RequestBody Order newOrder, @PathVariable Long id){
        if(orderValidator.run(newOrder)) {
            Order order = newOrder;
            try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
                session.beginTransaction();
                order = session.merge(newOrder);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            EntityModel<Order> model = assembler.toModel(order);
            return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id){
        try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
            session.beginTransaction();
            Order order = session.get(Order.class, id);
            session.remove(order);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.noContent().build();
    }
}
