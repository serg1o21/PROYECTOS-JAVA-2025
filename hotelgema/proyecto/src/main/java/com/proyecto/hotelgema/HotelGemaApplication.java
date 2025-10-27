package com.proyecto.hotelgema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class HotelGemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelGemaApplication.class, args);
	}

}
