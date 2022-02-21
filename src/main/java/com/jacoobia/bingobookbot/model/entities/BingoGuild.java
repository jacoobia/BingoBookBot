package com.jacoobia.bingobookbot.model.entities;

import com.jacoobia.bingobookbot.model.messages.MessageReceiver;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity(name = "guild")
public class BingoGuild extends MessageReceiver {

    private static final long serialVersionUID = -7956087528234502234L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "guild_id")
    private String guildId;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "bingo_running")
    private Boolean bingoRunning;

    @Column(name = "bingo_name")
    private String bingoName;

    @Column(name = "secret")
    private String secret;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Item> items;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "bingo_users")
    private List<User> users;

    @OneToMany(mappedBy = "guild")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SkillTarget> skills;

    /** Transient Fields */

    @Transient
    private Guild guild;

}
