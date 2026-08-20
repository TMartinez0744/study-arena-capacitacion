package edu.studyarena.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TrainingApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(TrainingApplication.class, args);
	}

}
