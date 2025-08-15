package com.codingame.game;

import com.codingame.gameengine.module.entities.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class will store all the required constants.
 */
public class Constants {
    // Define view height and width.
    public static final int VIEWER_WIDTH = World.DEFAULT_WIDTH;
    public static final int VIEWER_HEIGHT = World.DEFAULT_HEIGHT;

    // Define a tile size and offset.
    public static final int CELL_SIZE = 256;
    public static final int CONNECTOR_WIDTH = 64;
    public static final int CONNECTOR_OFFSET= Constants.CELL_SIZE/2 - CONNECTOR_WIDTH/2;

    // Background Sprite.
    public static final String BACKGROUND_SPRITE = "bg.png";

    // Regular and error tile sprites.
    public static final String TILE_SPRITE = "tile.png";
    public static final Map<Character, String> ERROR_TILE_MAPPER;
    static {
        Map<Character, String> map = new HashMap<>();
        map.put('.', "tileError.png");
        map.put('1', "tile1Error.png");
        map.put('2', "tile2Error.png");
        map.put('3', "tile3Error.png");
        map.put('4', "tile4Error.png");
        map.put('5', "tile5Error.png");
        map.put('6', "tile6Error.png");
        map.put('7', "tile7Error.png");
        map.put('8', "tile8Error.png");
        map.put('9', "tile9Error.png");
        ERROR_TILE_MAPPER = Collections.unmodifiableMap(map);
    }

    // Successful tiles sprites
    public static final Map<Character, String> SUCCESS_TILE_MAPPER;
    static {
        Map<Character, String> map = new HashMap<>();
        map.put('.', "tileComplete.png");
        map.put('1', "tile1Complete.png");
        map.put('2', "tile2Complete.png");
        map.put('3', "tile3Complete.png");
        map.put('4', "tile4Complete.png");
        map.put('5', "tile5Complete.png");
        map.put('6', "tile6Complete.png");
        map.put('7', "tile7Complete.png");
        map.put('8', "tile8Complete.png");
        map.put('9', "tile9Complete.png");
        SUCCESS_TILE_MAPPER = Collections.unmodifiableMap(map);
    }

    // Starting / Finishing blocks.
    public static final Map<Character, String> START_TILE_MAPPER;
    static {
        Map<Character, String> map = new HashMap<>();
        map.put('1', "start1.png");
        map.put('2', "start2.png");
        map.put('3', "start3.png");
        map.put('4', "start4.png");
        map.put('5', "start5.png");
        map.put('6', "start6.png");
        map.put('7', "start7.png");
        map.put('8', "start8.png");
        map.put('9', "start9.png");
        START_TILE_MAPPER = Collections.unmodifiableMap(map);
    }

    // Connectors - Horizontal.
    public static final Map<Character, String> HORIZONTAL_CONNECTOR_MAPPER;
    static {
        Map<Character, String> map = new HashMap<>();
        map.put('1', "connect_horizontal1.png");
        map.put('2', "connect_horizontal2.png");
        map.put('3', "connect_horizontal3.png");
        map.put('4', "connect_horizontal4.png");
        map.put('5', "connect_horizontal5.png");
        map.put('6', "connect_horizontal6.png");
        map.put('7', "connect_horizontal7.png");
        map.put('8', "connect_horizontal8.png");
        map.put('9', "connect_horizontal9.png");
        HORIZONTAL_CONNECTOR_MAPPER = Collections.unmodifiableMap(map);
    }

    // Connectors - Vertical.
    public static final Map<Character, String> VERTICAL_CONNECTOR_MAPPER;
    static {
        Map<Character, String> map = new HashMap<>();
        map.put('1', "connect_vertical1.png");
        map.put('2', "connect_vertical2.png");
        map.put('3', "connect_vertical3.png");
        map.put('4', "connect_vertical4.png");
        map.put('5', "connect_vertical5.png");
        map.put('6', "connect_vertical6.png");
        map.put('7', "connect_vertical7.png");
        map.put('8', "connect_vertical8.png");
        map.put('9', "connect_vertical9.png");
        VERTICAL_CONNECTOR_MAPPER = Collections.unmodifiableMap(map);
    }
}
