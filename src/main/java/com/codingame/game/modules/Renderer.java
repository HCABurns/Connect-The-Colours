package com.codingame.game.modules;

import com.codingame.game.*;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;

import java.io.Serializable;
import java.util.*;

public class Renderer implements Module {
    private final SoloGameManager<Player> gameManager;
    private final List<Map<String, Serializable>> allTiles = new ArrayList<>();
    private final GraphicEntityModule graphicEntityModule;
    private final Group group;
    private final ArrayList<Tile> tiles = new ArrayList<>();// Constants


    @Inject
    public Renderer(SoloGameManager<Player> gameManager, GraphicEntityModule graphicEntityModule) {
        this.gameManager = gameManager;
        this.graphicEntityModule = graphicEntityModule;
        int z_BACKGROUND = 0;
        graphicEntityModule.createSprite().setImage(Constants.BACKGROUND_SPRITE).setZIndex(z_BACKGROUND);
        group = graphicEntityModule.createGroup();
        gameManager.registerModule(this);
    }


    public void addErrorTile(int id, String texture) {
        for (Map<String, Serializable> map : allTiles){
            if (map.get("id") == (Serializable) id){
                map.replace("texture", texture);
            }
        }
    }


    public void setErrorTiles(Board board){
        for (Coordinate coord : board.getErrorTiles()){
            if (coord.getY() * board.getWidth() + coord.getX() < board.getWidth() * board.getHeight()) {
                addErrorTile(tiles.get(coord.getY() * board.getWidth() + coord.getX()).getId(), Constants.ERROR_TILE_MAPPER.get(board.getStartGrid().get(coord.getY())[coord.getX()]));
            }
        }
    }

    public void addCompletedTiles(Board board) {
        for (Map<String, Serializable> map : allTiles){
            map.replace("texture", Constants.SUCCESS_TILE_MAPPER.get(map.get("identifier")));
        }
    }

    public void drawTile(char number, int i, int j){
        String tileName = Constants.TILE_SPRITE;
        if (Constants.START_TILE_MAPPER.containsKey(number)){
            tileName = Constants.START_TILE_MAPPER.get(number);
        }
        int z_TILES = 5;
        Sprite tile = graphicEntityModule.createSprite()
                .setImage(tileName)
                .setX(j * Constants.CELL_SIZE)
                .setY(i * Constants.CELL_SIZE)
                .setAnchor(0)
                .setZIndex(z_TILES);

        tiles.add(new Tile(tile.getId(), number, tile));
        addTile(tile.getId(), tileName, number);
        group.add(tile);
    }

    public void drawConnector(int y1, int x1, int y2, int x2, char number){
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
        for (int i = 0; i <= connectors_to_build; i++) {
            int z_CONNECTORS = 10;
            if (vertical_direction != 0 && i != connectors_to_build) {
                sprite = graphicEntityModule.createSprite()
                        .setImage(ySprite)
                        .setX((x1) * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET)
                        .setY((y1 + (i * vertical_direction)) * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET)
                        .setAnchor(0)
                        .setZIndex(z_CONNECTORS)
                        .setScale(1);
                group.add(sprite);
            }
            else if (horizontal_direction != 0 && i != connectors_to_build) {
                sprite = graphicEntityModule.createSprite()
                        .setImage(xSprite)
                        .setX((x1 + i * horizontal_direction) * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET)
                        .setY(y1 * (Constants.CELL_SIZE) + Constants.CONNECTOR_OFFSET)
                        .setAnchor(0)
                        .setZIndex(z_CONNECTORS)
                        .setScale(1);
                group.add(sprite);
            }
        }
    }

    public void addTile(int id, String texture, char number){
        Map<String, Serializable> tile = new HashMap<>();
        tile.put("id", id);
        tile.put("texture", texture);
        tile.put("identifier", number);
        allTiles.add(tile);
    }

    public void scaleGroup(int w, int h){
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

        group.setScale(scale);
        group.setX(centerX - scaledWidth / 2);
        group.setY(centerY - scaledHeight / 2);
    }

    public Group getGroup(){
        return this.group;
    }

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
