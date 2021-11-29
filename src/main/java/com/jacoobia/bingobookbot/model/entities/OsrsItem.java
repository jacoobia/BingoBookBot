package com.jacoobia.bingobookbot.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OsrsItem implements Serializable {

    private static final long serialVersionUID = -6012833637919152831L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost")
    private String value;

}
