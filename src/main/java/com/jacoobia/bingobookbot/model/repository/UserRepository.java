package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByRsName(String username);

    Optional<User> findByDiscordId(String id);

}