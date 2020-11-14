package org.tbk.bitcoin.txstats.example.score.bitcoinabuse.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.DisposableBean;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class BtcAbuseApiClientImpl implements BtcAbuseApiClient, DisposableBean {

    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiToken;

    private final CloseableHttpClient client = HttpClients.createDefault();

    public BtcAbuseApiClientImpl(String baseUrl, String apiToken) {
        this(baseUrl, apiToken, new ObjectMapper());
    }

    public BtcAbuseApiClientImpl(String baseUrl, String apiToken, ObjectMapper objectMapper) {
        this.objectMapper = requireNonNull(objectMapper);
        this.baseUrl = requireNonNull(baseUrl);
        this.apiToken = requireNonNull(apiToken);
    }

    @Override
    public CheckResponseDto check(String address) {
        String url = String.format("%s/%s?api_token=%s&address=%s", baseUrl, "api/reports/check", apiToken, address);
        try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            try (InputStream is = entity.getContent()) {
                Map<String, Object> val = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
                });
                return CheckResponseDto.builder()
                        .address((String) val.get("address"))
                        .count((int) val.get("count"))
                        .build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AbuseType> abuseTypes() {
        String url = String.format("%s/%s?api_token=%s", baseUrl, "api/abuse-types", apiToken);
        try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            try (InputStream is = entity.getContent()) {
                List<Map<String, Object>> values = objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {
                });
                return values.stream()
                        .map(val -> AbuseType.builder()
                                .id((long) val.get("id"))
                                .label((String) val.get("label"))
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadCsv(DownloadDuration duration, FileOutputStream outputStream) {
        String url = String.format("%s/%s/%s?api_token=%s",
                baseUrl, "api/download", duration.getDuration(), apiToken);

        try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                entity.writeTo(outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        client.close();
    }
}
