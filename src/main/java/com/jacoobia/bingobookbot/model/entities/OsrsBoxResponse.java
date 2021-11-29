package com.jacoobia.bingobookbot.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class OsrsBoxResponse implements Serializable {

    private static final long serialVersionUID = -6012833637919152831L;

    @JsonProperty("_items")
    private List<OsrsItem> items;

}
