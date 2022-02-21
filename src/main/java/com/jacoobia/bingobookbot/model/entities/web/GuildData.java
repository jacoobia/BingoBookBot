package com.jacoobia.bingobookbot.model.entities.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GuildData implements Serializable {

    private static final long serialVersionUID = -5029288989451335683L;

    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("guild_name")
    private String guildName;

    @JsonProperty("member_count")
    private Integer guildMemberCount;

    @JsonProperty("bingo_active")
    private Boolean bingoActive;

    @JsonProperty("admin_access")
    private Boolean adminAccess;

    @JsonProperty("image_source")
    private String imageSource;

}
