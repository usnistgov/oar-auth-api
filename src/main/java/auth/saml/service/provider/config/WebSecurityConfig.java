package auth.saml.service.provider.config;



import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import auth.saml.service.provider.config.JWTConfig.JWTAuthenticationFilter;
import auth.saml.service.provider.config.JWTConfig.JWTAuthenticationFilterLocal;
import auth.saml.service.provider.config.JWTConfig.JWTAuthenticationProvider;
import auth.saml.service.provider.config.SAMLConfig.CORSFilter;
import auth.saml.service.provider.config.SAMLConfig.SamlSecurityConfig;

/**
 * In this configuration all the end points which need to be secured under
 * authentication service are added. This configuration also sets up token
 * generator and token authorization related configuration and end point
 * 
 * @author Deoyani Nandrekar-Heinis
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	/**
	 * The following configuration should get loaded only in local profile. This is
	 * to test locally without connecting the identity server.
	 * 
	 * @author Deoyani Nandrekar-Heinis
	 *
	 */
	@Configuration
//	@Profile({ "local" }) //This setting can be used to enable the feature based on certain profiles/platforms.
	@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "false", matchIfMissing = false)
	public class SecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity security) throws Exception {

			logger.info("#### SAML authentication and authorization service is disabled in this mode. #####");
			security.httpBasic().disable();
			security.formLogin().disable();
			security.cors().and().csrf().disable();
			security.authorizeRequests().antMatchers("/").permitAll();
		}

		/**
		 * Allow following URL patterns without any authentication and authorization
		 */
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
					"/configuration/security", "/swagger-ui.html", "/webjars/**", "/pdr/lp/draft/**");
		}
	}

	/**
	 * This bean is created only in local profile this avoids using external SAML id
	 * server.
	 */
	@Configuration
//	@Profile({ "local" }) //This setting can be used to enable the feature based on certain profiles/platforms.
	@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "false", matchIfMissing = false)
	@Order(1)
	public static class RestApiSecurityConfigLocal extends WebSecurityConfigurerAdapter {
		private Logger logger = LoggerFactory.getLogger(RestApiSecurityConfigLocal.class);

		@Value("${jwt.secret:testsecret}")
		String secret;

		private static final String apiMatcher = "/pdr/lp/editor/**";

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			logger.info("#### RestApiSecurityConfig HttpSecurity for REST /pdr/lp/editor/ endpoints ###");
			http.addFilterBefore(new JWTAuthenticationFilterLocal(apiMatcher, super.authenticationManager()),
					UsernamePasswordAuthenticationFilter.class);

			http.formLogin().disable();
			http.httpBasic().and().csrf().disable();

		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) {
			auth.authenticationProvider(new JWTAuthenticationProvider(secret));
		}
	}

	/**
	 * Rest security configuration for rest api
	 */
	@Configuration
	// @Profile({ "prod", "dev", "test", "default" }) //This setting can be used to
	// enable the feature based on certain profiles/platforms.
	@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "true", matchIfMissing = true)
	@Order(1)
	public static class RestApiSecurityConfig extends WebSecurityConfigurerAdapter {
		private Logger logger = LoggerFactory.getLogger(RestApiSecurityConfig.class);

		@Value("${jwt.secret:testsecret}")
		String secret;

		private static final String apiMatcher = "/pdr/lp/editor/**";

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			logger.info("#### RestApiSecurityConfig HttpSecurity for REST /pdr/lp/editor/ endpoints ###");
			http.addFilterBefore(new JWTAuthenticationFilter(apiMatcher, super.authenticationManager()),
					UsernamePasswordAuthenticationFilter.class);

			http.authorizeRequests().antMatchers(HttpMethod.PATCH, apiMatcher).permitAll();
			http.authorizeRequests().antMatchers(HttpMethod.GET, apiMatcher).permitAll();
			http.authorizeRequests().antMatchers(HttpMethod.DELETE, apiMatcher).permitAll();
			http.authorizeRequests().antMatchers(apiMatcher).authenticated().and().httpBasic().and().csrf().disable();

		}

		/**
		 * Authentication provider configuration
		 */
		@Override
		protected void configure(AuthenticationManagerBuilder auth) {
			auth.authenticationProvider(new JWTAuthenticationProvider(secret));
		}
	}

	/**
	 * Security configuration for authorization end pointsq
	 */
	@Configuration
	@Order(2)
	public static class AuthSecurityConfig extends WebSecurityConfigurerAdapter {
		private Logger logger = LoggerFactory.getLogger(AuthSecurityConfig.class);

		private static final String apiMatcher = "/auth/**";

//		@Autowired
//		private CustomAccessDeniedHandler accessDeniedHandler;
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			logger.info("Set up authorization related entrypoints.");

			http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
//			http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
			http.authorizeRequests().antMatchers(HttpMethod.GET, apiMatcher).permitAll().anyRequest().authenticated();
//			http.addFilterBefore(corsFiltertest(), SessionManagementFilter.class);
//			http.authorizeRequests()
//	            .antMatchers(HttpMethod.OPTIONS, "/auth/**")
//	            .permitAll()
//	            .anyRequest()
//	            .authenticated()
//	            .and()
//	            .httpBasic();
		}
		
		@Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOrigins(Arrays.asList("*"));
	        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
	        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
	        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }
	
	}



	/**
	 * Saml security config
	 */
	@Configuration
//	@Profile({ "prod", "dev", "test", "default" }) //This setting can be used to enable the feature based on certain profiles/platforms.
	@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "true", matchIfMissing = true)
	@Import(SamlSecurityConfig.class)
	public static class SamlConfig {

	}
	
	

}