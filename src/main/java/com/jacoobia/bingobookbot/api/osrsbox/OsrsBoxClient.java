package com.jacoobia.bingobookbot.api.osrsbox;

import com.jacoobia.bingobookbot.model.entities.osrs.OsrsBoxResponse;
import com.jacoobia.bingobookbot.model.entities.osrs.OsrsItem;
import org.springframework.web.client.RestTemplate;

public class OsrsBoxClient {

    private static final String ITEM_URL = "https://api.osrsbox.com/items?where={criterion}";
    private static final String ITEM_ICON_IRL = "https://www.osrsbox.com/osrsbox-db/items-icons/%itemId%.png";

    /**
     * Contact the OSRS Box Endpoint and check if there's an item
     * with the given name
     * @param name the item name
     * @return the response object
     */
    public OsrsBoxResponse lookup(String name) {
        RestTemplate restTemplate = new RestTemplate();
        String criterion = "{\"name\":\"%item%\"}".replace("%item%", name);
        return restTemplate.getForObject(ITEM_URL, OsrsBoxResponse.class, criterion);
    }

    public OsrsItem lookupItem(String name) {
        return lookup(name).getItems().get(0);
    }

    public static String getIconUrl(OsrsItem item) {
        return ITEM_ICON_IRL.replace("%itemId%", item.getId());
    }

}
