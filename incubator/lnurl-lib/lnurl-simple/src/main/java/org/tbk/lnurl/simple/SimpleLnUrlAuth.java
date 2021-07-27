package org.tbk.lnurl.simple;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.tbk.lnurl.K1;
import org.tbk.lnurl.LnUrl;
import org.tbk.lnurl.LnUrlAuth;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Slf4j
@Value
public class SimpleLnUrlAuth implements LnUrlAuth {
    public static final String TAG_PARAM_VALUE = "login";

    String baseUrl;
    K1 k1;
    Action action;

    private SimpleLnUrlAuth(String baseUrl) {
        this(baseUrl, SimpleK1.random());
    }

    private SimpleLnUrlAuth(String baseUrl, K1 k1) {
        this(baseUrl, k1, null);
    }

    private SimpleLnUrlAuth(String baseUrl, K1 k1, Action action) {
        this.baseUrl = requireNonNull(baseUrl);
        this.k1 = requireNonNull(k1);
        this.action = action;
    }

    // https://example.com?tag=login&k1=hex(32 bytes of random data)&action=login
    public static SimpleLnUrlAuth create(String url) {
        return new SimpleLnUrlAuth(url, SimpleK1.random());
    }

    public static SimpleLnUrlAuth from(LnUrl lnurl) {
        return from(lnurl.toUri());
    }

    public static SimpleLnUrlAuth parse(String uri) {
        return from(URI.create(uri));
    }

    public static SimpleLnUrlAuth from(URI uri) {
        requireNonNull(uri, "'uri' must not be null");

        if (!LnUrl.isSupported(uri)) {
            throw new IllegalArgumentException("Unsupported url: Only 'https' or 'onion' urls allowed");
        }

        List<NameValuePair> queryParams = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);

        Map<String, List<NameValuePair>> queryParamsMap = queryParams.stream()
                .collect(Collectors.groupingBy(NameValuePair::getName));

        List<NameValuePair> tagQueryParams = queryParamsMap.getOrDefault("tag", Collections.emptyList());
        checkArgument(tagQueryParams.size() == 1, "Url must include exactly one 'tag' query parameter");
        String tag = tagQueryParams.stream().map(NameValuePair::getValue).findFirst().orElseThrow();
        checkArgument(TAG_PARAM_VALUE.equals(tag), "Invalid 'tag' query parameter: Must have value '" + TAG_PARAM_VALUE + "'");

        List<NameValuePair> k1QueryParams = queryParamsMap.getOrDefault("k1", Collections.emptyList());
        checkArgument(k1QueryParams.size() == 1, "Url must include exactly one 'k1' query parameter");
        String k1 = k1QueryParams.stream().map(NameValuePair::getValue).findFirst().orElseThrow();

        List<NameValuePair> actionQueryParams = queryParamsMap.getOrDefault("action", Collections.emptyList());
        checkArgument(actionQueryParams.size() <= 1, "Url must not include more than one 'action' query parameter");
        Optional<Action> action = actionQueryParams.stream()
                .map(NameValuePair::getValue)
                .map(Action::parse)
                .findFirst();

        return new SimpleLnUrlAuth(uri.toString(), SimpleK1.fromHexString(k1), action.orElse(null));
    }

    @Override
    @SneakyThrows(URISyntaxException.class)
    public URI toUri() {
        URIBuilder uriBuilder = new URIBuilder(baseUrl)
                .setParameter("tag", TAG_PARAM_VALUE)
                .setParameter("k1", k1.hex());

        this.getAction().ifPresent(it -> uriBuilder.setParameter("action", it.getValue()));

        return uriBuilder.build();
    }

    @Override
    public LnUrl toLnUrl() {
        return SimpleLnUrl.encode(toUri());
    }

    @Override
    public Optional<Action> getAction() {
        return Optional.ofNullable(action);
    }

}
