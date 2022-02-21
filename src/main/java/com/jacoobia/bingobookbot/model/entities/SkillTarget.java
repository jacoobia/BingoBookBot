package com.jacoobia.bingobookbot.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "skill_target")
public class SkillTarget implements Serializable {

    private static final long serialVersionUID = -773112837960042164L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    private Skill skill;

    @Column(name = "xp_target")
    private Integer xpTarget;

    @ManyToOne
    @JoinColumn(name="bingo_id", nullable=false)
    private BingoGuild guild;

}
