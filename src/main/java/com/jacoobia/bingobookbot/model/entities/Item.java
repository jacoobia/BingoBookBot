package com.jacoobia.bingobookbot.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "item")
public class Item implements Serializable {

    private static final long serialVersionUID = 7122532995543295565L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "osrs_id")
    private Integer osrsId;

    @Column(name = "url")
    private String url;

}