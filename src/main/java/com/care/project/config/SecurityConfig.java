package com.care.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.care.project.filter.JwtAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource()).and()  // ✅ CORS 설정을 직접 적용
            .csrf().disable()  // ✅ CSRF 비활성화 (CORS 문제 방지)
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Preflight OPTIONS 요청 허용
            .antMatchers("/root/**").authenticated()  // ✅ 특정 엔드포인트는 인증 필요
            .anyRequest().permitAll()  // ✅ 그 외 요청 허용
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // ✅ 세션을 사용하지 않음
        
        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // ✅ Spring Security와 함께 동작하도록 CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 특정 도메인만 허용:
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
