package com.jacoobia.bingobookbot.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "bingo_skill_target")
public class BingoSkillTarget implements Serializable {

    private static final long serialVersionUID = -773112837960042164L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "bingo_guild_id")
    private String bingoGuildId;

    @OneToOne
    private BingoSkill skill;

    @Column(name = "xp_target")
    private Integer xpTarget;

}
