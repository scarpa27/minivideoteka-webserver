package hr.tvz.tim2.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebserverApplication.class, args);
	}

}
