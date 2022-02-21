package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> findByName(String name);

}