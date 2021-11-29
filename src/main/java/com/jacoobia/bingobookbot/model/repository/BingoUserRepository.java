package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.BingoUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BingoUserRepository extends JpaRepository<BingoUser, Integer> {

    BingoUser findByRsName(String username);

    BingoUser findByDiscordId(String id);

}