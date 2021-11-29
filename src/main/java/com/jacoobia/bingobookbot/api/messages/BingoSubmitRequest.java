package com.jacoobia.bingobookbot.api.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BingoSubmitRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("date")
    private String date;

    @SerializedName("secret")
    private String secret;

    @SerializedName("imageData")
    private String imageData;

}