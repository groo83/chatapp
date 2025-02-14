package com.groo.chatapp.config;


import com.groo.chatapp.common.jwt.JwtAccessDeniedHandler;
import com.groo.chatapp.common.jwt.JwtAuthenticationEntryPoint;
import com.groo.chatapp.common.jwt.JwtFilter;
import com.groo.chatapp.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        .requestMatchers("/","/files/**","/main","/register","/api/**", "/home", // todo "/files/**" 제거 후 main.js 에서 downloadLink click 이벤트로 구현 필요
                                "/js/**","/ws-stomp/**").permitAll() // 회원가입 허용
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 요청 페이지
                        .usernameParameter("email") // 사용자 ID 파라미터
                        //.passwordParameter("password") // 비밀번호 파라미터
                        //.defaultSuccessUrl("/main", true) // 로그인 성공 시 이동할 URL
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
                                (SessionCreationPolicy.STATELESS) // 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
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
    public WebSecurityCustomizer configureH2ConsoleAndSwaggerEnable() {
        return web -> web.ignoring()
                .requestMatchers("/h2-console/**", // JUnit 테스트 시 오류로 경로 하드코딩 (기존 : PathRequest.toH2Console())
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**"
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
