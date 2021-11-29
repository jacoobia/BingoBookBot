package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.BingoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BingoItemRepository extends JpaRepository<BingoItem, Integer> {

    BingoItem findByName(String name);

}