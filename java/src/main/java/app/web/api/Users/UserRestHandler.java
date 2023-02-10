package app.web.api.Users;

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
public class UserRestHandler {
    private final UserModelAssembler assembler;
    private final Validator<User> userValidator =
                    new Validator<>((user) -> user.userId != 0,
                    new Validator<>((user) -> user.username != null,
                    new Validator<>((user) -> !user.username.isBlank())));

    UserRestHandler(UserModelAssembler assembler) {
        this.assembler = assembler;
    }


    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllUsers(){
        List<EntityModel<User>> list = new ArrayList<>();

        try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("from User", User.class).list().forEach(User -> {
                if(userValidator.run(User)) {
                    list.add(assembler.toModel(User));
                }
            });
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CollectionModel.of(list, linkTo(methodOn(UserRestHandler.class).getAllUsers()).withSelfRel());
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> getUser(@PathVariable Long id){
        User User = null;

        try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
            session.beginTransaction();
            User = session.get(User.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert User != null;
        return assembler.toModel(User);
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user){
        if(userValidator.run(user)) {
            try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.persist(user);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            EntityModel<User> model = assembler.toModel(user);
            return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> putUser(@RequestBody User newUser, @PathVariable Long id){
        if(userValidator.run(newUser)) {
            User user = newUser;
            try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
                session.beginTransaction();
                user = session.merge(newUser);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            EntityModel<User> model = assembler.toModel(user);
            return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, id);
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.noContent().build();
    }
}
