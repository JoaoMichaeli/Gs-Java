package com.gs.EcoDenuncia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(title = "EcoDenuncia API", version = "v1", description = "API do SaaS EcoDenuncia Gs2025")
)
@EnableCaching
public class EcoDenunciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcoDenunciaApplication.class, args);
	}

}
