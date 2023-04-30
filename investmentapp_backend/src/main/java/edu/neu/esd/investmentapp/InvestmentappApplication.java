package edu.neu.esd.investmentapp;

import edu.neu.esd.investmentapp.entities.Investor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class InvestmentappApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentappApplication.class, args);
//		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		System.out.println("Hello World");
	}

	@RequestMapping(value = "/")
	public String helloStudents(){
		return "Hello Students";
	}

}
