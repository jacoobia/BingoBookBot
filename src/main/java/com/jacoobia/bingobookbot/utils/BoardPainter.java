package com.jacoobia.bingobookbot.utils;

import com.jacoobia.bingobookbot.model.entities.Item;
import com.jacoobia.bingobookbot.model.entities.Skill;
import com.jacoobia.bingobookbot.model.entities.SkillTarget;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class BoardPainter {

    private static final float TILES_PER_ROW = 5.0f;
    private static final int TILE_WIDTH = 126;
    private static final int TILE_HEIGHT = 105;

    private static final BufferedImage TILE_BACKGROUND;

    private final List<BufferedImage> tiles = new ArrayList<>();

    static {
        TILE_BACKGROUND = ImageUtils.loadImage("classpath:static/img/button.png", true);
    }

    /**
     * Takes a {@link Item} and creates a {@link BufferedImage} tile and
     * adds it to the internet list of tiles ready to be painted later.
     *
     * @param item the bingo item to create a tile for
     * @return this object
     */
    public BoardPainter add(final Item item) {
        BufferedImage icon = ImageUtils.loadImage(item.getUrl(), false);
        if(icon != null) {
            BufferedImage tile = paintTile(icon, item.getName());
            tiles.add(tile);
        }
        return this;
    }

    /**
     * Takes a {@link SkillTarget} and creates a {@link BufferedImage} tile and
     * adds it to the internet list of tiles ready to be painted later.
     *
     * @param target the bingo target xp object to create a tile for
     * @return this object
     */
    public BoardPainter add(final SkillTarget target) {
        Skill skill = target.getSkill();
        BufferedImage icon = ImageUtils.loadImage("https://oldschool.runescape.wiki/images/" + skill.getImageUrl(), false);
        if(icon != null) {
            String name = StringUtils.convertShorthand(target.getXpTarget()) + " XP";
            BufferedImage tile = paintTile(icon, name);
            tiles.add(tile);
        }
        return this;
    }

    /**
     * Paints/Constructs the final board image as a {@link BufferedImage} object
     *
     * @return the painted board
     */
    public BufferedImage paint() {
        int rows = (int) Math.ceil(tiles.size() / TILES_PER_ROW);
        final int width = (int) (TILE_WIDTH * TILES_PER_ROW);
        final int height = TILE_HEIGHT * rows;

        BufferedImage board = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics2D boardGraphics = board.createGraphics();
        Collections.shuffle(tiles);
        for (BufferedImage tile : tiles) {
            attachTile(tile, boardGraphics);
        }
        boardGraphics.dispose();
        return board;
    }

    /**
     * Paints the final board and converts it to a byte array to be sent across
     * the network by the bot.
     *
     * @return the byte array data for the board
     */
    public byte[] paintAndEncode() {
        BufferedImage board = paint();
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(board, "png", output);
            output.flush();
            return output.toByteArray();
        }
        catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * Creates a combined painted tile for a bingo item
     *
     * @param icon the icon for the tile
     * @param name the name of the item
     * @return a buffered image object for the tile
     */
    private BufferedImage paintTile(BufferedImage icon, String name) {
        BufferedImage combined = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, TYPE_INT_ARGB);
        Graphics2D graphics = combined.createGraphics();

        int iconX = (TILE_WIDTH / 2) - (icon.getWidth() / 2);
        int iconY = (TILE_HEIGHT / 2) - (icon.getHeight() / 2);

        graphics.drawImage(TILE_BACKGROUND, 0, 0, null);
        graphics.drawImage(icon, iconX, iconY, null);

        drawLabel(name, graphics);
        return combined;
    }

    /**
     * Attaches a tile to a {@link Graphics2D} object placing it
     * depending on the index of the tile and some maths trickery.
     * Thanks to Hector (ImStratss) for the rubber duck debugging
     *
     * @param tile the tile {@link BufferedImage} to attach
     * @param graphics the graphics object to paint the tile onto
     */
    private void attachTile(BufferedImage tile, Graphics2D graphics) {
        int index = tiles.indexOf(tile);
        int row = (int) (index / TILES_PER_ROW);
        int column = (int) (index % TILES_PER_ROW);
        int x = TILE_WIDTH * column;
        int y = TILE_HEIGHT * row;
        graphics.drawImage(tile, x, y, null);
    }

    /**
     * Draws a text label onto a {@link Graphics2D} object
     *
     * @param label the text to draw
     * @param graphics the graphics object
     */
    private void drawLabel(String label, Graphics2D graphics) {
        configureFont(graphics);
        graphics.setColor(new Color(255, 255, 0));
        FontMetrics fm = graphics.getFontMetrics();
        int x = (TILE_WIDTH - fm.stringWidth(label)) / 2;
        int y = (TILE_HEIGHT - (fm.getAscent()) - 5);
        graphics.drawString(label, x, y);
    }

    /**
     * Configures the font for a specified {@link Graphics2D} object
     *
     * @param graphics the graphics object to configure it for
     */
    private void configureFont(Graphics2D graphics) {
        try {
            File fontFile = ResourceUtils.getFile("classpath:static/fonts/osrs.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f);
            graphics.setFont(font);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
