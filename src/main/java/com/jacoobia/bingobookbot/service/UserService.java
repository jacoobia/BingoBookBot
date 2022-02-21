package com.jacoobia.bingobookbot.service;

import com.jacoobia.bingobookbot.model.entities.User;
import com.jacoobia.bingobookbot.model.repository.UserRepository;
import com.jacoobia.bingobookbot.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;

    /**
     * Find a user by their in-game OSRS username
     * @param name the users username
     * @return a {@link User} object
     */
    public User findUser(String name) {
        Optional<User> found = userRepository.findByRsName(name);
        return found.orElse(null);
    }

    /**
     * Find a user by their discord ID
     * @param id the users discord ID
     * @return a {@link User} object
     */
    public User find(String id) {
        Optional<User> found = userRepository.findByDiscordId(id);
        return found.orElse(null);
    }

    /**
     * Find a user by their Bingo Book
     * @param id the users Bingo Book
     * @return a {@link User} object
     */
    public User find(Integer id) {
        Optional<User> found = userRepository.findById(id);
        return found.orElse(null);
    }

    /**
     * Creates a new {@link User} object and saves it
     * to the database and returns it
     * @param username the osrs username of the user
     * @param discordId the discord ID of the user
     * @return a newly created {@link User} object
     */
    public User createUser(String username, String discordId) {
        User user = new User();
        user.setRsName(username);
        user.setDiscordId(discordId);
        user.setBingosCompleted(NumberUtils.INTEGER_ZERO);
        userRepository.save(user);
        return user;
    }

}
