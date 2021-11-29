package com.jacoobia.bingobookbot.api.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BingoConnectRequest {

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("username")
    private String username;

}
