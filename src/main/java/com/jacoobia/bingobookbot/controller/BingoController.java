package com.jacoobia.bingobookbot.controller;

import com.jacoobia.bingobookbot.api.messages.BingoApiResponse;
import com.jacoobia.bingobookbot.api.messages.BingoConnectRequest;
import com.jacoobia.bingobookbot.api.messages.BingoSubmitRequest;
import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.model.entities.User;
import com.jacoobia.bingobookbot.model.messages.MessageSenderrr;
import com.jacoobia.bingobookbot.service.GuildService;
import com.jacoobia.bingobookbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@RestController
@RequestMapping("bingo")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BingoController {

    private final GuildService guildService;
    private final MessageSenderrr messageSender;

    private final UserService userService;

    @PostMapping("/submit")
    public void submit(@RequestBody BingoSubmitRequest submitRequest) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] rawImageData = decoder.decode(submitRequest.getImageData());

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawImageData);
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            BingoGuild guild = guildService.getBySecret(submitRequest.getSecret());
            if (guild != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, "png", output);
                output.flush();
                String message = submitRequest.getUsername() + " has completed a bingo tile!";
                String imageName = submitRequest.getDate() + "-" + submitRequest.getUsername();
                messageSender.sendMessageWithImage(guild, message, imageName, output.toByteArray());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/connect")
    public BingoApiResponse connect(@RequestBody BingoConnectRequest request) {
        BingoApiResponse response = new BingoApiResponse();
        BingoGuild guild = guildService.getBySecret(request.getSecret());
        if (guild != null) {
            User user = userService.findUser(request.getUsername());
            if(user != null) {
                response.setResponse(0);
            }
            else response.setResponse(1);
            //else not registered
        }
        else response.setResponse(1);
        //else invalid secret
        return response;
    }

}
