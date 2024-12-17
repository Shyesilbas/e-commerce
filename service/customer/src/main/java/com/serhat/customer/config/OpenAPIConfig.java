package com.serhat.customer.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customerServiceApi(){
        return new OpenAPI()
                .info(new Info().title("Customer Service Documentation")
                        .description("REST API")
                        .version("v1.0")
                )
                .externalDocs(new ExternalDocumentation().description("Check Api")
                        .url("http://localhost:8070/api-docs"));
    }


}
