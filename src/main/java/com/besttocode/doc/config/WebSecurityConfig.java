package com.besttocode.doc.config;

import com.besttocode.doc.config.security.DocAuthFilter;
import com.besttocode.doc.config.security.DocAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DocAuthProvider docAuthProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(docAuthProvider)
                .addFilterBefore(docAuthFilter(), AnonymousAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .cors();
    }

    private DocAuthFilter docAuthFilter() throws Exception {

        OrRequestMatcher orRequestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/doc/**")
        );

        DocAuthFilter docAuthFilter = new DocAuthFilter(orRequestMatcher);
        docAuthFilter.setAuthenticationManager(authenticationManager());

        return docAuthFilter;
    }

}
