package auth.saml.service.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UrlPathHelper;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
@ComponentScan(basePackages = { "auth.saml.service.provider"})
@RefreshScope
public class ServiceProviderApplication implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(ServiceProviderApplication.class, args);
	}
	
	@Bean
    public OpenAPI samlProviderAPI(@Value("1.1.0") String appVersion) {
	
	List<Server> servers = new ArrayList<Server>();
	servers.add(new Server().url("/samlprovider"));
	String description = "These are set of APIs which are used by data publishing workflow to edit new dataset metadata records.";
		
	
       return new OpenAPI()
        .components(new Components().addSecuritySchemes("basicScheme",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
	       .components(new Components()).servers(servers)
        .info(new Info().title("SAML Service Provider")
                .description(description)
                .version(appVersion)             
                .license(new License().name("Software Name").url("<licence url>")));
    }



    
//    @SuppressWarnings("deprecation")
//	@Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**").allowedOrigins("http://localhost:4200");
//            }
//        };
//    }
	/**
     * CORS configuration
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200"
                )
                .allowedMethods(
                        "GET",
                        "PUT",
                        "POST",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                );
    }


}