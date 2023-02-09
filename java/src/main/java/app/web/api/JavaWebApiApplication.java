package app.web.api;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
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

	public static void main(String[] args) {
		Log.setLogger(new LoggerConfiguration()
				.writeTo(coloredConsole())
				.writeTo(seq("http://seq:5341"))
				.createLogger());

		SpringApplication.run(JavaWebApiApplication.class, args);

		Log.information("Started java web api!");

		DaprClient daprClient = new DaprClientBuilder().build();
		daprClient.waitForSidecar(30 * 1000).block();

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure()
				.build();

		try{
			SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();

			Session session = sessionFactory.openSession();

			session.beginTransaction();

			session.persist(Order.builder().price(100d).userID(new Random().nextLong()).orderDate(new Date()));

			session.getTransaction().commit();
			session.close();


			session = sessionFactory.openSession();
			session.beginTransaction();
			List result = session.createQuery( "from Order" ).list();
			for ( Order order : (List<Order>) result ) {
				System.out.println(order);
			}
			session.getTransaction().commit();
			session.close();



		}catch (Exception e){
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
		}

		while(true) {}
	}
}
