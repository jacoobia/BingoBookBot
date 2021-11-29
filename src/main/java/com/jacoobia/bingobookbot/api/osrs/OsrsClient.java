package com.jacoobia.bingobookbot.api.osrs;

import org.springframework.web.client.RestTemplate;

public class OsrsClient {

    private static final String HISCORES_ENDPOINT = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%username%";
    private static final String USERNAME_REPLACER = "%username%";
    private static final String SPACE_REPLACER = "%20";

    public boolean userExists(String user) {
        RestTemplate restTemplate = new RestTemplate();
        final String url = HISCORES_ENDPOINT.replace(USERNAME_REPLACER, user).replace(" ", SPACE_REPLACER);
        try {
            return restTemplate.getForObject(url, String.class) != null;
        } catch (Exception e) {
            return false;
        }
    }

}