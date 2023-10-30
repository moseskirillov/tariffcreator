package com.fmlogistic.tariffcreator.models.unisender;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendRequest {
    @JsonProperty("api_key")
    private String apiKey;
    private Message message;
}
