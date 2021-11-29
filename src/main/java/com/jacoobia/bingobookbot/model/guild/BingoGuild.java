package com.jacoobia.bingobookbot.model.guild;

import com.jacoobia.bingobookbot.model.entities.BingoItem;
import com.jacoobia.bingobookbot.model.entities.BingoSkillTarget;
import com.jacoobia.bingobookbot.model.entities.BingoUser;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "bingo_guild")
public class BingoGuild implements Serializable {

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
    @CollectionTable(name = "bingo_items")
    private List<BingoItem> items;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "bingo_users")
    private List<BingoUser> users;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "bingo_skill_targets")
    private List<BingoSkillTarget> skills;

    /** Transient Fields */

    @Transient
    private Guild guild;

    @Transient
    private MessageChannel channel;

}
