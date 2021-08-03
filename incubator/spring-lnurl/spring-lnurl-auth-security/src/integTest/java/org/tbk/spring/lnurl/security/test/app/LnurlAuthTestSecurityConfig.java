package org.tbk.spring.lnurl.security.test.app;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.tbk.lnurl.auth.LnurlAuthFactory;
import org.tbk.lnurl.auth.SimpleK1Manager;
import org.tbk.lnurl.auth.SimpleLnurlAuthFactory;
import org.tbk.spring.lnurl.security.LnurlAuthConfigurer;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@EnableWebSecurity
@Configuration
public class LnurlAuthTestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        web.httpFirewall(new StrictHttpFirewall());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService())
                .apply(new LnurlAuthConfigurer())
                .k1Manager(k1Manager())
                .pairingService(pairingService())
                .and()
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                        .sessionFixation().migrateSession()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/login?logout")
                )
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .antMatchers("/").permitAll()
                        .antMatchers("/login").permitAll()
                        .antMatchers(LnurlAuthConfigurer.defaultWalletLoginUrl()).permitAll()
                        .antMatchers(LnurlAuthConfigurer.defaultSessionLoginUrl()).permitAll()
                        .anyRequest().authenticated()
                );
    }

    @Bean
    public LnurlAuthFactory lnurlAuthFactory() {
        // any url well do - we just need the query string in tests
        URI loginUrl = URI.create("https://localhost" + LnurlAuthConfigurer.defaultWalletLoginUrl());

        return new SimpleLnurlAuthFactory(loginUrl, k1Manager());
    }

    @Bean
    public TestPairingService pairingService() {
        return new TestPairingService(userDetailsService());
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    // make bean of K1Manager injectable in tests
    @Bean
    public SimpleK1Manager k1Manager() {
        return new SimpleK1Manager();
    }

}
