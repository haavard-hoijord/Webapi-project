package app.web.api.Users;

import app.web.api.JavaWebApiApplication;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static app.web.api.Users.UserRestHandler.userValidator;

public class UserHandler {
	public static List<User> getAllUsers() {
		List<User> list = new ArrayList<>();

		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.createQuery("from User", User.class).list().forEach(User -> {
				if (userValidator.run(User)) {
					list.add(User);
				}
			});
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static User getUser(Long id) {
		User User = null;

		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			User = session.get(User.class, id);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return User;
	}

	public static void addUser(User user) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.persist(user);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void putUser(User newUser, Long id) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.merge(newUser);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteUser(Long id) {
		try (Session session = JavaWebApiApplication.getSessionFactory().openSession()) {
			session.beginTransaction();
			User user = session.get(User.class, id);
			session.remove(user);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
