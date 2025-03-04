package com.groo.chatapp.config;


import com.groo.chatapp.security.constants.SecurityConstants;
import com.groo.chatapp.security.jwt.JwtAccessDeniedHandler;
import com.groo.chatapp.security.jwt.JwtAuthenticationEntryPoint;
import com.groo.chatapp.security.jwt.JwtFilter;
import com.groo.chatapp.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                // .authorizeHttpRequests(auth -> auth
                //        .anyRequest().permitAll());

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 요청 페이지
                        .usernameParameter("email") // 사용자 ID 파라미터
                        .permitAll()
                )
                .logout(logout -> logout
                    //.logoutUrl("/logout") // 로그아웃 URL
                    //.logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 리다이렉트 URL // default
                    //.invalidateHttpSession(true) // 세션 무효화
                    //.deleteCookies("JSESSIONID") // 쿠키 삭제
                    .permitAll()
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy
                                (SessionCreationPolicy.STATELESS) // 세션 미사용하므로, Stateless 로 설정
                ).exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // H2 DB, Swagger Spring Security ignoring
    @Bean
    public WebSecurityCustomizer configureIgnore() {
        return web -> web.ignoring()
                .requestMatchers(SecurityConstants.IGNORE_WHITELIST);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
