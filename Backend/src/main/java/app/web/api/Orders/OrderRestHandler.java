package app.web.api.Orders;

import app.web.api.Validator;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderRestHandler {
	private final OrderModelAssembler assembler;

	public static final Validator<Order> orderValidator = Validator.of(
			(order) -> order.orderDate != null,
			(order) -> order.user != null,
			(order) -> order.getPrice() >= 0);


	OrderRestHandler(OrderModelAssembler assembler) {
		this.assembler = assembler;
	}

	@GetMapping("/orders")
	public CollectionModel<EntityModel<Order>> getAllOrders() {
		return CollectionModel.of(OrderHandler.getAllOrders().stream().map(assembler::toModel).toList(), linkTo(methodOn(OrderRestHandler.class).getAllOrders()).withSelfRel());
	}

	@GetMapping("/orders/{id}")
	public EntityModel<Order> getOrder(@PathVariable Long id) {
		return assembler.toModel(OrderHandler.getOrder(id));
	}

	@PostMapping("/orders")
	public ResponseEntity<?> addOrder(@RequestBody Order order) {
		if (orderValidator.run(order)) {
			OrderHandler.addOrder(order);
			EntityModel<Order> model = assembler.toModel(order);
			return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/orders/{id}")
	public ResponseEntity<?> putOrder(@RequestBody Order newOrder, @PathVariable Long id) {
		if (orderValidator.run(newOrder)) {
			OrderHandler.putOrder(newOrder, id);
			EntityModel<Order> model = assembler.toModel(newOrder);
			return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/orders/{id}")
	public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
		OrderHandler.deleteOrder(id);
		return ResponseEntity.noContent().build();
	}
}
