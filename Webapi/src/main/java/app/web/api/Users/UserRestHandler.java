package app.web.api.Users;

import app.web.api.Validator;
import kong.unirest.json.JSONObject;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserRestHandler {
	private final UserModelAssembler assembler;
	public static final Validator<User> userValidator = Validator.of(
			(user) -> user.username != null,
			(user) -> !user.username.isBlank());

	UserRestHandler(UserModelAssembler assembler) {
		this.assembler = assembler;
	}


	@GetMapping("/users")
	public CollectionModel<EntityModel<User>> getAllUsers() {
		return CollectionModel.of(UserHandler.getAllUsers().stream().map(assembler::toModel).toList(), linkTo(methodOn(UserRestHandler.class).getAllUsers()).withSelfRel());
	}

	@GetMapping("/users/{id}")
	public EntityModel<User> getUser(@PathVariable Long id) {
		return assembler.toModel(UserHandler.getUser(id));
	}

	@PostMapping("/users")
	public ResponseEntity<?> addUser(@RequestBody User user) {
		if (userValidator.run(user) && UserHandler.getAllUsers().stream().noneMatch(s -> s.getUsername().equalsIgnoreCase(user.getUsername()))) {
			UserHandler.addUser(user);
			EntityModel<User> model = assembler.toModel(user);
			return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<?> putUser(@RequestBody User newUser, @PathVariable Long id) {
		if (userValidator.run(newUser) && UserHandler.getAllUsers().stream().noneMatch(s -> s.getUsername().equalsIgnoreCase(newUser.getUsername()))) {
			UserHandler.putUser(newUser, id);
			EntityModel<User> model = assembler.toModel(newUser);
			return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		UserHandler.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
