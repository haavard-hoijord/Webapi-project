package app.web.api;

import app.web.api.Products.Product;
import app.web.api.Products.ProductHandler;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.Getter;
import net.datafaker.Faker;
import org.apache.tomcat.util.buf.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import serilogj.Log;
import serilogj.LoggerConfiguration;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static serilogj.sinks.coloredconsole.ColoredConsoleSinkConfigurator.coloredConsole;
import static serilogj.sinks.seq.SeqSinkConfigurator.seq;

@SpringBootApplication
@OpenAPIDefinition(info = @Info( title = "E-Commerce API", version = "1.0", description = "Store API"))
public class JavaWebApiApplication {

	@Getter
	private static SessionFactory sessionFactory;

	@Getter
	private static SpringApplication app;

	@Getter
	private static ConfigurableApplicationContext appContext;

	@Getter
	private static DaprClient daprClient;

	@Getter
	private static final String stateStore = "statestore";

	@Getter
	private static final String pubSub = "redis-pubsub";

	private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	public static void main(String[] args) {
		Log.setLogger(new LoggerConfiguration()
				.writeTo(coloredConsole())
				.writeTo(seq("http://seq:5341"))
				.createLogger());

		app = new SpringApplication(JavaWebApiApplication.class);
		app.setBannerMode(Banner.Mode.OFF);

		appContext = app.run(args);

		Log.information("Started java web api!");

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure()
				.build();

		try{
			daprClient = new DaprClientBuilder().build();
			daprClient.waitForSidecar(10 * 1000).block();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
			return;
		}

		Faker faker = new Faker();
		int ProductCount = 200;

		if (ProductHandler.getAllProducts().size() < ProductCount) {
			for (int i = 0; i < ProductCount - ProductHandler.getAllProducts().size(); i++) {
				Random random = new Random();
				ProductHandler.addProduct(Product.builder()
						.productName(faker.commerce().productName())
						.productImage("https://picsum.photos/seed/product" + i + "/200")
						.productDescription(StringUtils.join(faker.lorem().sentences(2), ' '))
						.price(Double.valueOf(faker.commerce().price()))
						.stock(random.nextInt(200)).build());
			}
		}

		//Restock products every 5min
		executorService.scheduleWithFixedDelay(() -> {
			for(Product product : ProductHandler.getAllProducts()){
				if(product.getStock() <= 0){
					System.out.println("Restocking " + product.productId + " - " + product.productName);
					Random random = new Random();
					product.setStock(random.nextInt(200));
					ProductHandler.putProduct(product);
				}
			}
		}, 5, 5, TimeUnit.MINUTES);

		while (true) {}
	}
}