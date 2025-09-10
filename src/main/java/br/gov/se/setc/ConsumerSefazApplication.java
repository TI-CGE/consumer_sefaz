package br.gov.se.setc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class ConsumerSefazApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConsumerSefazApplication.class, args);
	}
}