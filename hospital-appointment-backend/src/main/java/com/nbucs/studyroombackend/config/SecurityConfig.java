package com.nbucs.studyroombackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/api/auth/**").permitAll()
                        .antMatchers("/api/reservation/**").permitAll()
                        .antMatchers("/api/studyRoomManage/**").permitAll()
                        .antMatchers("/api/seminarRoomManage/**").permitAll()
                        .antMatchers("/api/seatManage/**").permitAll()
                        .antMatchers("/api/attendance/**").permitAll()
                        .antMatchers("/api/violation/**").permitAll()
                        .antMatchers("/api/wait/**").permitAll()
                        .antMatchers("/api/notification/**").permitAll()
                        .antMatchers("/api/seminar-room/**").permitAll()
                        .antMatchers("/api/feedback/**").permitAll()
                        .antMatchers("/api/student/**").permitAll()
                        // 新增医院系统接口放行
                        .antMatchers("/api/hospital/**").permitAll()
                        .antMatchers("/api/debug/**").permitAll()
                        .antMatchers("/api/department/**").permitAll()
                        .antMatchers("/api/doctor/**").permitAll()
                        .antMatchers("/api/patient/**").permitAll()
                        .antMatchers("/api/schedule/**").permitAll()
                        .antMatchers("/api/appointment/**").permitAll()
                        .antMatchers("/api/review/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
