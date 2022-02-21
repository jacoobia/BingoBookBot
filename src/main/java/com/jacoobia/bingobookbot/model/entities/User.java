package com.jacoobia.bingobookbot.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 5950204675104775824L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "osrs_username")
    private String rsName;

    @Column(name = "bingo_count")
    private Integer bingosCompleted;

}