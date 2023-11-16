package auth.saml.service.provider.config.SAMLConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter helps identify the origin of request, allows only the listed URLs
 * to send authentication request. Helps further communication based on token
 * exchage.
 * 
 * @author Deoyani Nandrekar-Heinis
 *
 */
public class CustomCORSFilter implements Filter {

	private String[] allowedURLs = new String[0];

	public CustomCORSFilter() {
	}

	public CustomCORSFilter(String listURLs) {
		
		allowedURLs = listURLs.split(",");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

                // List<String> allowedOrigins = Arrays.asList(allowedURLs);
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		// Access-Control-Allow-Origin
		String origin = request.getHeader("Origin");
                for(String appurl : allowedURLs) {
                    try {
                        URL url = new URL(appurl);
                        url = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
                        if (origin == null || origin.isEmpty())
                            origin = url.toString();
                        if (origin.startsWith(url.toString())) {
                            response.setHeader("Access-Control-Allow-Origin", origin);
                            break;
                        }
                    } catch (MalformedURLException ex) { }
                }
                
		response.setHeader("Vary", "Origin");

		// Access-Control-Max-Age
		response.setHeader("Access-Control-Max-Age", "3600");

		// Access-Control-Allow-Credentials
		response.setHeader("Access-Control-Allow-Credentials", "true");

		// Access-Control-Allow-Methods
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");

		// Access-Control-Allow-Headers
		response.setHeader("Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept, withCredentials" + "X-CSRF-TOKEN");

		filterChain.doFilter(request, response);

	}

	@Override
	public void destroy() {

	}
}
