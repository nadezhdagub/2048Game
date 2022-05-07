package src;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Tile {

    int value;
    static Map<Integer, Integer> colors = new HashMap<>();

    static {
        colors.put(0, 0xcdc1b4);
        colors.put(2, 0xeee4da);
        colors.put(4, 0xede0c8);
        colors.put(8, 0xf2b179);
        colors.put(16, 0xf59563);
        colors.put(32, 0xf67c5f);
        colors.put(64, 0xf65e3b);
        colors.put(128, 0xedcf72);
        colors.put(256, 0xedcc61);
        colors.put(512, 0xedc850);
        colors.put(1024, 0xedc53f);
        colors.put(2048, 0xedc22e);
    }

    public Tile() {
        this.value = 0;
    }

    public Tile(int value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public Color getFontColor() {
        if (value < 16) return new Color(0x776e65);
        else return new Color(0xf9f6f2);
    }

    public Color getTileColor() {
        return new Color(colors.getOrDefault(value, 0xff0000));
    }
}
