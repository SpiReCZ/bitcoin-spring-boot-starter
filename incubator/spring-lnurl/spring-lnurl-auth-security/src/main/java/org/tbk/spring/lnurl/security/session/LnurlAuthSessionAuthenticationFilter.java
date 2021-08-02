package org.tbk.spring.lnurl.security.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.tbk.lnurl.auth.K1;
import org.tbk.lnurl.simple.auth.SimpleK1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
public class LnurlAuthSessionAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final String k1AttributeName;

    public LnurlAuthSessionAuthenticationFilter(String pathRequestPattern, String k1AttributeName) {
        this(new AntPathRequestMatcher(pathRequestPattern, HttpMethod.GET.name()), k1AttributeName);
    }

    protected LnurlAuthSessionAuthenticationFilter(AntPathRequestMatcher pathRequestPattern, String k1AttributeName) {
        super(pathRequestPattern);

        Assert.hasText(k1AttributeName, "k1AttributeName cannot be empty");
        this.k1AttributeName = k1AttributeName;
        this.setAllowSessionCreation(false); // session must only be created by the application itself
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Optional<K1> k1 = obtainK1(request);

        if (log.isDebugEnabled()) {
            log.debug("got lnurl-auth session migration request for k1 '{}'", k1.map(K1::toHex).orElse(null));
        }

        if (k1.isEmpty()) {
            throw new AuthenticationServiceException("'k1' is missing or invalid.");
        }

        LnurlAuthSessionToken lnurlAuthSessionToken = new LnurlAuthSessionToken(k1.get());

        setDetails(request, lnurlAuthSessionToken);

        return this.getAuthenticationManager().authenticate(lnurlAuthSessionToken);
    }

    protected Optional<K1> obtainK1(HttpServletRequest request) {
        return Optional.of(request)
                .map(HttpServletRequest::getSession)
                .map(it -> (String) it.getAttribute(k1AttributeName))
                .map(SimpleK1::fromHex);
    }

    protected void setDetails(HttpServletRequest request, LnurlAuthSessionToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}