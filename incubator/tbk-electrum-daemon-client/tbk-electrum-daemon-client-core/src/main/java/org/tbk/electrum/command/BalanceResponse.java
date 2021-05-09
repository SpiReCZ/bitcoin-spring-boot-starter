package org.tbk.electrum.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Optional;

@Data
@Setter(AccessLevel.NONE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceResponse {

    @JsonProperty("confirmed")
    private String confirmed;

    @JsonProperty("unconfirmed")
    private String unconfirmed;

    @JsonProperty("unmatured")
    private String unmatured;

    public Optional<String> getUnconfirmed() {
        return Optional.ofNullable(unconfirmed);
    }

    public Optional<String> getUnmatured() {
        return Optional.ofNullable(unmatured);
    }
}
