package com.jacoobia.bingobookbot.service;

import com.jacoobia.bingobookbot.api.osrsbox.OsrsBoxClient;
import com.jacoobia.bingobookbot.model.entities.Item;
import com.jacoobia.bingobookbot.model.entities.osrs.OsrsItem;
import com.jacoobia.bingobookbot.model.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Find an {@link Item} by its name
     * @param name the name of the item
     * @return the item found or null
     */
    public Item find(String name) {
        Optional<Item> found = itemRepository.findByName(name);
        return found.orElse(null);
    }

    /**
     * Create and return a new {@link Item} from an OsrsBox
     * lookup parsed into an {@link OsrsItem} object
     * @param osrsItem the {@link OsrsItem} object
     * @return a newly create item object
     */
    public Item createItem(OsrsItem osrsItem){
        Item item = new Item();
        item.setName(osrsItem.getName());
        item.setUrl(OsrsBoxClient.getIconUrl(osrsItem));
        item.setOsrsId(Integer.valueOf(osrsItem.getId()));
        itemRepository.save(item);
        return item;
    }

}
