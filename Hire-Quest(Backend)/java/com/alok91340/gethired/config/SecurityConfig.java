
package com.alok91340.gethired.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alok91340.gethired.security.CustomUserDetailsService;
import com.alok91340.gethired.security.JwtAuthenticationEntryPoint;
import com.alok91340.gethired.security.JwtAuthenticationFilter;


/**
 * @author alok91340
 *
 */

@Configuration
public class SecurityConfig {

    public static final String[] PUBLIC_URLS = {
    		"/v3/api-docs",
            "/api/hireQuest/create-user",
            "/api/hireQuest/user-login",
            "/api/hireQuest/generate",
            "/api/hireQuest/{email}/send-otp",
            "/api/hireQuest/{email}/resend-otp",
            "/api/hireQuest/{otpCode}/{email}/otp-verification",
            "/api/hireQuest/{email}/check-email",
            "/api/hireQuest/{username}/check-username",
            "/api/hireQuest/{email}/{password}/change-password",
            "/ws/{token}",
            "api/hireQuest/{username}/get-fcm-token",
            "api/hireQuest/user-logout",
            "api/hireQuest/google-sign-in",
            "api/hireQuest/update-fcm-token"
    };

    @Autowired(required = false)
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                /*-------------------------JWT Starts------------------------------*/
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                /*-------------------------JWT ends------------------------------*/
                .and()
                .authorizeHttpRequests()
                // to permit all get request and secure post put and delete methods
                .requestMatchers("/swagger-ui/**", "/v3/api-docs", "/configuration/**", "/webjars/**").permitAll()
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v*/contact/**").permitAll()
                .anyRequest()
                .authenticated();

        /**
         * Basic auth used before JWT implementation
         * .and()
         * .httpBasic();
         **/
        http
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Jwt auth filter method
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

  
    // DB Auth
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // User authentication manager bean
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}