package org.tbk.lightning.lnurl.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tbk.lnurl.simple.SimpleLnUrlAuth;
import org.tbk.tor.hs.HiddenServiceDefinition;

import java.net.URI;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class LnurlAuthExampleApplicationConfig {

    @Bean
    @Profile("!test")
    public ApplicationRunner applicationHiddenServiceInfoRunner(HiddenServiceDefinition applicationHiddenServiceDefinition) {
        return args -> {
            String onionUrl = applicationHiddenServiceDefinition.getVirtualHost()
                    .map(val -> {
                        int port = applicationHiddenServiceDefinition.getVirtualPort();
                        if (port == 80) {
                            return "http://" + val;
                        } else if (port == 443) {
                            return "https://" + val;
                        }
                        return "http://" + val + ":" + applicationHiddenServiceDefinition.getVirtualPort();
                    }).orElseThrow();

            SimpleLnUrlAuth lnUrlAuth = SimpleLnUrlAuth.create(URI.create(onionUrl));

            log.info("=================================================");
            log.info("===== LNURL_AUTH ================================");
            log.info("=================================================");
            log.info("login page: {}", onionUrl + "/login");
            log.info("=================================================");
            log.info("example auth url: {}", lnUrlAuth.toUri().toString());
            log.info("=================================================");
        };
    }
}
