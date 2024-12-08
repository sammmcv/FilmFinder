package net.codejavaspring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;


@Configuration
public class WebSecurityConfig {
    
    @Bean // resttemplate para el API 
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean // con esto podemos trabajar con roles y credenciales
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean // definicion del codificador
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // autenticacion de los usuarios al iniciar sesion
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean // Define el SecurityFilterChain para manejar la seguridad
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF si no es necesario
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN") // Rutas restringidas al rol ADMIN
                .requestMatchers("/users").hasRole("ADMIN")
                .requestMatchers("/consumeApiMovies").authenticated()
                .requestMatchers("/consumeApiBooks").hasRole("ADMIN")
                .requestMatchers("/welcome").authenticated() // Autenticación para /welcome
                .requestMatchers("/api").authenticated()
                .requestMatchers("/movie/**").authenticated()
                .requestMatchers("/editUser/**").hasRole("ADMIN")
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/oauth2/authorization/**").permitAll() // Permite el acceso sin autenticación
                .anyRequest().permitAll() // Permite el resto de rutas
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // Página de login personalizada
                .defaultSuccessUrl("/welcome", true) // Redirección tras login exitoso con Google
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService()) // Define nuestro servicio de usuario OAuth2 personalizado
                )
            )
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/welcome", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login") // Redirige a /login tras cerrar sesión
                .invalidateHttpSession(true) // Invalida la sesión actual
                .clearAuthentication(true) // Limpia la autenticación
                .deleteCookies("JSESSIONID", "remember-me") // Asegura que las cookies de sesión sean eliminadas
                .permitAll()
            );

        return http.build();
    }
    
    @Bean
    @Primary
    public CustomUserDetailsService customOAuth2UserService() {
        return new CustomUserDetailsService(); // Nuestro servicio personalizado para manejar roles y usuarios
    }
    
    @Bean // para obtener el AuthenticationManager
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
