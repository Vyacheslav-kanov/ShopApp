package app.shop;
import app.shop.model.Discount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
public class ShopAppApplication {

	public static final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ru.sber.ServiceTransferMoney");

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(ShopAppApplication.class, args);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			Discount discount = new Discount(new ArrayList<>(), 0.02f, new Date());
			String json = ow.writeValueAsString(discount);
			ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

			System.out.println(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
