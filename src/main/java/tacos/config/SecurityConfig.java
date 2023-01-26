package tacos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tacos.exceptions.UsernameNotFoundException;
import tacos.security.User;
import tacos.repositories.UserRepository;
import tacos.security.UserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return username -> {
            User user = repository.findByUsername(username);
            if (user != null) {
                return user;
            }

            throw new UsernameNotFoundException("User '" + username + "' not found.");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorize -> {
                            try {
                                authorize
                                        .requestMatchers("/design", "/orders").hasRole("ROLE_USER")
                                        .requestMatchers("/", "/**").permitAll()
                                        .and()
                                            .formLogin()
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/design", true);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .build();
    }

}
