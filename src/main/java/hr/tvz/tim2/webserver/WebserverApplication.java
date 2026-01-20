package hr.tvz.tim2.webserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class WebserverApplication {

	public static void main(String[] args) {
		log.info("Starting application...");
		SpringApplication.run(WebserverApplication.class, args);
	}

}
