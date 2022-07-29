package cl.gonzalo.app;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder registrarWebClient(){

        return WebClient.builder().baseUrl("http://servicio-productos/api/v2/productos");
    }

}
