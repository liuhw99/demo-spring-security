package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
	// @formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
			.anyRequest().authenticated()
			.and()
		.formLogin()
			//.loginPage("/login")
			.permitAll()
			.and()
		.logout()
			.permitAll();       
	}
	// @formatter:on

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(User.withUsername("user")
				.password("{noop}password").roles("USER").build());
	}
	
  	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName(IAMConst.IAM_COOKIE_NAME); 
		serializer.setDomainNamePattern("^.+?\\.(.+\\.[a-z]+)$");
		return serializer;
	}
	
    // Handler deciding where to redirect user after successful login
    @Bean
    public AuthenticationSuccessHandler successRedirectHandler() {
    	SimpleUrlAuthenticationSuccessHandler successRedirectHandler =
                new SimpleUrlAuthenticationSuccessHandler();
        successRedirectHandler.setTargetUrlParameter("goto");
        return successRedirectHandler;
    }
}