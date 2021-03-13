package com.example.bootiful;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class BootifulApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootifulApplication.class, args);
	}
}


@Entity
class Customer {

	@Id
	@GeneratedValue
	private Integer id;

	private String name;

	public Customer() {
	}

	public Customer(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Customer customer = (Customer) o;
		return Objects.equals(id, customer.id) && Objects.equals(name, customer.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}

interface CustomerRepository extends JpaRepository<Customer, Integer> {
}

@Component
class CustomHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		return Health.status("I <3 Spring Boot!").build();
	}
}

@RestController
class CustomerRestController {

	private final CustomerRepository customerRepository;

	CustomerRestController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@GetMapping("/customers")
	Collection<Customer> get() {
		return this.customerRepository.findAll();
	}
}

@Component
class CustomerRunner implements ApplicationRunner {

	public CustomerRunner(CustomerRepository repos) {
		this.repos = repos;
	}

	private final CustomerRepository repos;

	@Override
	public void run(ApplicationArguments args) {
		List.of("A", "B", "C", "D")
			.stream()
			.map(name -> new Customer(null, name))
			.map(repos::save)
			.forEach(c -> System.out.println("saved " + c.getId() + '.'));
		repos.findAll().forEach(System.out::println);
	}
}