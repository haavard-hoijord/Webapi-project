package app.web.api;

import app.web.api.Orders.Order;
import app.web.api.Users.User;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import serilogj.Log;
import serilogj.LoggerConfiguration;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static serilogj.sinks.coloredconsole.ColoredConsoleSinkConfigurator.*;

import static serilogj.sinks.seq.SeqSinkConfigurator.seq;

@SpringBootApplication
public class JavaWebApiApplication {

	@Getter
	private static SessionFactory sessionFactory;

	public static void main(String[] args) {
		Log.setLogger(new LoggerConfiguration()
				.writeTo(coloredConsole())
				.writeTo(seq("http://seq:5341"))
				.createLogger());

		SpringApplication.run(JavaWebApiApplication.class, args);

		Log.information("Started java web api!");

		try (DaprClient daprClient = new DaprClientBuilder().build()) {
			daprClient.waitForSidecar(30 * 1000).block();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure()
				.build();


		try{
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		}catch (Exception e){
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
			return;
		}

		User user = User.builder().username("user123").build();
		Order order = Order.builder().price(100d).user(user).orderDate(new Date()).build();
		user.setOrders(List.of(order));


		try(Session session = getSessionFactory().openSession()){
			session.beginTransaction();

			session.persist(user);
			session.persist(order);

			session.getTransaction().commit();
		}

		while(true) {}
	}
}
