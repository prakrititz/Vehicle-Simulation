package radiant.seven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Random;

@SpringBootApplication
public class SevenApplication {
	private final Random random = new Random();

	public static void main(String[] args) {
		SpringApplication.run(SevenApplication.class, args);
	}

}