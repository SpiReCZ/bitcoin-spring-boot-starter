package org.tbk.lightning.lnurl.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.tbk.lnurl.auth.InMemoryK1Cache;
import org.tbk.lnurl.auth.LnurlAuthPairingService;
import org.tbk.lnurl.auth.SimpleK1Factory;
import org.tbk.lnurl.auth.SimpleK1Manager;
import org.tbk.spring.lnurl.security.LnurlAuthConfigurer;

@Slf4j
@EnableWebSecurity
@Configuration
public class LnurlAuthExampleApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    static final String LNURL_AUTH_WALLET_LOGIN_PATH = "/api/v1/lnauth/login/wallet";
    static final String LNURL_AUTH_SESSION_LOGIN_PATH = "/api/v1/lnauth/login/session";

    public static String lnurlAuthWalletLoginPath() {
        return LNURL_AUTH_WALLET_LOGIN_PATH;
    }

    public static String lnurlAuthSessionLoginPath() {
        return LNURL_AUTH_SESSION_LOGIN_PATH;
    }

    private final LnurlAuthPairingService lnurlAuthPairingService;

    private final UserDetailsService userDetailsService;

    public LnurlAuthExampleApplicationSecurityConfig(LnurlAuthPairingService lnurlAuthPairingService,
                                                     UserDetailsService userDetailsService) {
        this.lnurlAuthPairingService = lnurlAuthPairingService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService())
                .csrf().disable()
                .cors().disable()
                .apply(lnurlAuthConfigurer())
                .walletLoginUrl(lnurlAuthWalletLoginPath())
                .sessionLoginUrl(lnurlAuthSessionLoginPath())
                .and()
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                        .sessionFixation().migrateSession()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .headers(headers -> headers
                        .xssProtection()
                        .xssProtectionEnabled(true)
                        .block(true)
                        .and()
                        .contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'"))
                )
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .antMatchers("/").permitAll()
                        .antMatchers("/index.html").permitAll()
                        .antMatchers("/authenticated.html").authenticated()
                        // login page should be readable by all - session will be initialized
                        .antMatchers("/login").permitAll()
                        .antMatchers(lnurlAuthWalletLoginPath()).permitAll()
                        .antMatchers(lnurlAuthSessionLoginPath()).permitAll()
                        .antMatchers("/api/v1/lnauth/**").permitAll()
                        .anyRequest().authenticated()
                );
    }

    @Bean
    public LnurlAuthConfigurer lnurlAuthConfigurer() {
        return new LnurlAuthConfigurer(k1Manager(), lnurlAuthPairingService);
    }

    @Bean
    public SimpleK1Manager k1Manager() {
        return new SimpleK1Manager(k1Factory(), k1Cache());
    }

    @Bean
    public SimpleK1Factory k1Factory() {
        return new SimpleK1Factory();
    }

    @Bean
    public InMemoryK1Cache k1Cache() {
        return new InMemoryK1Cache();
    }
}
