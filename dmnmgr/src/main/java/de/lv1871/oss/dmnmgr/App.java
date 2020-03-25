package de.lv1871.oss.dmnmgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.lv1871.oss.dmnmgr.api.documentation.DecisionApi;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(value = { "de.lv1871.oss" })
@CrossOrigin
@EnableSwagger2
@Configuration
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	public Docket api() {
		return DecisionApi.createApiInfo();
	}
}
