package com.codingame.game.modules;

import com.codingame.game.*;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

import java.io.Serializable;
import java.util.*;

public class Renderer implements Module {
    // Set selection of variables that are used throughout.
    private final SoloGameManager<Player> gameManager;
    private final List<Map<String, Serializable>> allTiles = new ArrayList<>();
    private final GraphicEntityModule graphicEntityModule;
    private final Group group;
    private final ArrayList<Integer> tiles = new ArrayList<>();
    private final ArrayList<Sprite> debug_tiles = new ArrayList<>();
    private final TooltipModule tooltipModule;
    private int h;
    private int w;

    private final Group debug_group;
    /**
     * Create a Renderer object for adding the visuals to the screen.
     * @param gameManager - gameManager object.
     * @param graphicEntityModule - graphicEntityModule object.
     */
    @Inject
    public Renderer(SoloGameManager<Player> gameManager, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule) {
        this.gameManager = gameManager;
        this.graphicEntityModule = graphicEntityModule;
        this.tooltipModule = tooltipModule;
        int z_BACKGROUND = 0;
        graphicEntityModule.createSprite().setImage(Constants.BACKGROUND_SPRITE).setZIndex(z_BACKGROUND);
        group = graphicEntityModule.createGroup();
        debug_group = graphicEntityModule.createGroup();
        gameManager.registerModule(this);
    }


    /**
     * This function will create a sprite with the given parameters.
     * @param texture - String of the texture.
     * @param x - X-Position to place the sprite.
     * @param y - Y-Position to place the sprite.
     * @param z - Z-index of the sprite.
     * @param anchor - Double value of the anchor.
     * @return Generated Sprite object.
     */
    public Sprite createSprite(String texture, int x, int y, int z, int anchor){
        return graphicEntityModule.createSprite()
                .setImage(texture)
                .setX(x)
                .setY(y)
                .setAnchor(anchor)
                .setZIndex(z);
    }


    /**
     * Function to update the texture of a tile to be the provided texture.
     * @param id - ID of the sprite to be updated.
     * @param texture String of the texture to be updated to.
     */
    public void addErrorTile(int id, String texture) {
        for (Map<String, Serializable> map : allTiles){
            if (map.get("id") == (Serializable) id){
                map.replace("texture", texture);
            }
        }
    }


    /**
     * Sets the error tiles for the tiles that are set as involved in an error.
     * @param board - Board object containing information for getting which tiles need to be updated.
     */
    public void setErrorTiles(Board board){
        for (Coordinate coord : board.getErrorTiles()){
            if (coord.getY() * board.getWidth() + coord.getX() < board.getWidth() * board.getHeight()) {
                addErrorTile(tiles.get(coord.getY() * board.getWidth() + coord.getX()), Constants.ERROR_TILE_MAPPER.get(board.getStartGrid().get(coord.getY())[coord.getX()]));

                addErrorTile(debug_tiles.get(coord.getY() * board.getWidth() + coord.getX()).getId(), Constants.ERROR_TILE_MAPPER.get(board.getStartGrid().get(coord.getY())[coord.getX()]));

            }
        }
    }


    /**
     * Convert all tiles to their success counterpart.
     */
    public void addCompletedTiles() {
        for (Map<String, Serializable> map : allTiles){
            map.replace("texture", Constants.SUCCESS_TILE_MAPPER.get(map.get("identifier")));
        }
    }


    /**
     * Draw the tile and add to the group.
     * @param number - The number identifier of the tile.
     * @param i - Vertical position.
     * @param j - Horizontal position.
     */
    public void drawTile(char number, int i, int j){
        String tileName = Constants.TILE_SPRITE;
        if (Constants.START_TILE_MAPPER.containsKey(number)){
            tileName = Constants.START_TILE_MAPPER.get(number);
        }
        int z_TILES = 5;

        Sprite tile = createSprite(tileName, j * Constants.CELL_SIZE, i * Constants.CELL_SIZE, z_TILES, 0);
        Sprite debug_tile = createSprite(tileName, j * Constants.CELL_SIZE, i * Constants.CELL_SIZE, z_TILES, 0);

        tiles.add(tile.getId());
        addTile(tile.getId(), tileName, number, allTiles);
        group.add(tile);

        // Set debug tiles and tooltips.
        debug_group.add(debug_tile);
        debug_tiles.add(debug_tile);
        addTile(debug_tile.getId(), tileName, number, allTiles);
        if (number == '.') {
            tooltipModule.setTooltipText(debug_tile, ("x: " + j + "\ny: " + i + "\nconnections: 0" + "\ncolour: none"));
        }
        else{
            tooltipModule.setTooltipText(debug_tile, ("STARTING TILE\n"+"x: " + j + "\ny: " + i + "\nconnections: 0" + "\ncolour: " + number));
        }
    }


    /**
     * Adds a continuous connection between y1,x1 and y2,x2
     * @param y1 - Y-Coordinate of tile 1.
     * @param x1 - X-Coordinate of tile 1.
     * @param y2 - Y-Coordinate of tile 2.
     * @param x2 - X-Coordinate of tile2.
     * @param number - Colour identifier number.
     */
    public void drawConnector(int y1, int x1, int y2, int x2, char number, Map<Coordinate, Set<Coordinate>> connections){
        // Create the link between the moves.
        int horizontal_direction = 0;
        int vertical_direction = 0;
        String xSprite = Constants.HORIZONTAL_CONNECTOR_MAPPER.get(number);
        String ySprite = Constants.VERTICAL_CONNECTOR_MAPPER.get(number);

        if (y1 == y2){
            if (x1 < x2){horizontal_direction = 1;}
            else{horizontal_direction = -1;}
        }else{
            if (y1 < y2){vertical_direction = 1;}
            else{vertical_direction = -1;}
        }
        int connectors_to_build = Math.abs(y1 - y2) + Math.abs(x1 - x2);
        Sprite sprite = null;
        Sprite debug_sprite = null;
        for (int i = 0; i <= connectors_to_build; i++) {
            int z_CONNECTORS = 10;
            if (vertical_direction != 0 && i != connectors_to_build) {
                int x = (x1) * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET;
                int y = (y1 + (i * vertical_direction)) * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET;
                sprite = createSprite(ySprite, x, y, z_CONNECTORS, 0);
                debug_sprite = createSprite(ySprite, x, y, z_CONNECTORS, 0);

                group.add(sprite);

                // Update the debug tooltip.
                debug_group.add(debug_sprite);
                updateTooltip((y1 + (i * vertical_direction)) * this.w + x1, number);
                updateTooltip((y1 + ((i + 1) * vertical_direction)) * this.w + x1, number);
            }
            else if (horizontal_direction != 0 && i != connectors_to_build) {
                int x = (x1 + i * horizontal_direction) * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET;
                int y = y1 * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET;
                sprite = createSprite(xSprite, x, y, z_CONNECTORS, 0);
                debug_sprite = createSprite(xSprite, x, y, z_CONNECTORS, 0);

                group.add(sprite);

                // Update debug and tooltip.
                debug_group.add(debug_sprite);

                updateTooltip(y1 * this.w + (x1 + i * horizontal_direction), number);
                updateTooltip(y1 * this.w + (x1 + (1 + i) * horizontal_direction), number);
            }
            // Commit the connector at the START of the frame.
            graphicEntityModule.commitEntityState(0,sprite);
            graphicEntityModule.commitEntityState(0,debug_sprite);
            graphicEntityModule.commitEntityState(0,group);
            graphicEntityModule.commitEntityState(0,debug_group);
        }
    }


    /**
     * Function to update the colour of a tile for debug mode.
     * @param pos - Position in the 1D array.
     * @param colour - Colour to be updated.
     */
    public void updateTooltip(int pos , char colour){
        Sprite sprite = debug_tiles.get(pos);
        String[] arr = tooltipModule.getTooltipText(sprite).split("\n");

        String[] colours = arr[arr.length-1].split(" ");
        int connections_position = 2;
        // Add a new tooltip with the attempted colour change.
        if (!Objects.equals(colours[1], "none") && !Objects.equals(colours[1], String.valueOf(colour))){
            if (!Objects.equals(arr[arr.length-1].split(" ")[0], "attempted")) {
                String[] newArr = Arrays.copyOf(arr, arr.length + 1);
                newArr[arr.length] = "attempted colour: " + colour;
                arr = newArr;
            }
            connections_position += 1;
        }
        // Update the colour
        else if (Objects.equals(colours[1], "none")){
            arr[arr.length-1] = "colour: " + colour;
        }

        // Increment connections
        int connections = Integer.parseInt(String.valueOf(arr[arr.length-connections_position].split(" ")[1]));
        arr[arr.length-connections_position] = "connections: " + (connections+1);
        tooltipModule.setTooltipText(sprite, String.join("\n", arr));

    }


    /**
     * Add tile to the tiles list.
     * @param id - Tile's sprite ID.
     * @param texture - String of the texture of the tile.
     * @param number - Tiles colour identifier.
     */
    public void addTile(int id, String texture, char number, List<Map<String, Serializable>> list){
        Map<String, Serializable> tile = new HashMap<>();
        tile.put("id", id);
        tile.put("texture", texture);
        tile.put("identifier", number);
        list.add(tile);
    }


    /**
     * Scale the group to fit the screen.
     * @param w - Width of the board.
     * @param h - Height of the board.
     */
    public void scaleGroup(int w, int h){
        this.h = h;
        this.w = w;
        // Calculate total grid size in pixels
        int gridWidth = w * Constants.CELL_SIZE;
        int gridHeight = h * Constants.CELL_SIZE;

        // Calculate scale to fit in viewer
        double scaleX = (double) Constants.VIEWER_WIDTH / gridWidth;
        double scaleY = (double) Constants.VIEWER_HEIGHT / gridHeight;
        double scale = Math.min(1.0, Math.min(scaleX, scaleY));

        // Recompute size after scale for centering
        int scaledWidth = (int) (gridWidth * scale);
        int scaledHeight = (int) (gridHeight * scale);

        // Center the group in the viewer
        int centerX = Constants.VIEWER_WIDTH / 2;
        int centerY = Constants.VIEWER_HEIGHT / 2;

        // Scale and position both groups.
        group.setScale(scale);
        group.setX(centerX - scaledWidth / 2);
        group.setY(centerY - scaledHeight / 2);

        debug_group.setScale(scale);
        debug_group.setX(centerX - scaledWidth / 2);
        debug_group.setY(centerY - scaledHeight / 2);
    }

    public Group getGroup(){
        return this.group;
    }

    public Group getDebugGroup(){
        return this.debug_group;
    }

    /**
     * Return the Z index of the group.
     * @return Integer of the Z index of the group.
     */
    public int getZ_UI() {
        return 20;
    }

    @Override
    public void onAfterGameTurn() {
        Map<String, Serializable> data = new HashMap<>();
        data.put("tiles", (Serializable) allTiles);
        gameManager.setViewData("Renderer", data);
    }

    @Override
    public void onGameInit() {
        Map<String, Serializable> data = new HashMap<>();
        data.put("tiles", (Serializable) allTiles);
        gameManager.setViewData("Renderer", data);
    }

    @Override
    public void onAfterOnEnd() {

    }
}
