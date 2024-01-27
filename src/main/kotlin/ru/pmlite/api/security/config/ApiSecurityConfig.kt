package ru.pmlite.api.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import ru.pmlite.api.security.filters.AUTH_COOKIE_NAME
import ru.pmlite.api.security.filters.JWTAuthorizationFilter
import ru.pmlite.api.security.services.AuthenticationUserService


@Configuration
@EnableWebSecurity
class ApiSecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity, jwtAuthorizationFilter: JWTAuthorizationFilter): SecurityFilterChain {
        http
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf {
                it
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler().apply { setCsrfRequestAttributeName(null) })
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .rememberMe { it.disable() }
            .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .logout {
                it.deleteCookies("SESSION", AUTH_COOKIE_NAME)
                    .invalidateHttpSession(true)
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler { _, response, _ -> response.status = 200 }
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/registration/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/authorization/createToken").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/authorization/validateUserToken").permitAll()
                    .requestMatchers("/error").permitAll()

                    .requestMatchers("/api/authorization/isAuthorized").authenticated()

                    .requestMatchers("/api/**").hasRole("USER")

                    .anyRequest().authenticated()
            }
            .exceptionHandling { it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) }
        return http.build()
    }

    @Bean
    fun authenticationManager(
        userDetailService: AuthenticationUserService,
        passwordEncoder: BCryptPasswordEncoder
    ): AuthenticationManager {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(authProvider)
    }
}
