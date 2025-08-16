package com.codingame.game;

import java.util.*;

/**
 * Class for storing all information about the board and connections.
 */
public class Board {
    // Set required variables.
    private final int h;
    private final int w;
    private final ArrayList<char[]> grid = new ArrayList<>();
    private final ArrayList<char[]> startGrid = new ArrayList<>();
    private final Set<Coordinate> startColours = new HashSet<>();
    private final Set<Coordinate> allStartColours = new HashSet<>();
    private final Set<Integer> colourIdentifiers = new HashSet<>();
    private final Map<Coordinate, Set<Coordinate>> connections = new HashMap<>();
    private final Map<Character,Set<Coordinate>> paths = new HashMap<>();
    private int total_connected = 0;
    private final ArrayList<Coordinate> invalidTiles = new ArrayList<>();

    /**
     * Create and empty board.
     * @param h - Height of the board.
     * @param w - Width of the board.
     */
    public Board(int h, int w) {
        this.h = h;
        this.w = w;
    }

    /**
     * Create the connections between the two provided tiles. (Assumes they're valid)
     * @param y1 - Y-Coordinate of tile 1.
     * @param x1 - X-Coordinate of tile 1.
     * @param y2 - Y-Coordinate of tile 2.
     * @param x2 - X-Coordinate of tile2.
     * @param colourIdentifier - Char of the colour identifier.
     */
    public void addConnections(int y1, int x1, int y2, int x2, char colourIdentifier){
        int vertical_direction = 0;
        if (y1 != y2){
            if (y1 < y2){vertical_direction = 1;}
            else{vertical_direction = -1;}
        }
        int connectors_to_build = Math.abs(y1 - y2) + Math.abs(x1 - x2);
        for (int i = 0; i < connectors_to_build; i++) {
            Coordinate coord1;
            Coordinate coord2;
            if (vertical_direction != 0) {
                coord1 = new Coordinate(y1+i, x1, grid.get(y1+i)[x1]);
                coord2 = new Coordinate(y1+i+1, x1, grid.get(y1+i+1)[x1]);
            }else{
                coord1 = new Coordinate(y1, x1+i, grid.get(y1)[x1+i]);
                coord2 = new Coordinate(y1, x1+i+1, grid.get(y1)[x1+i+1]);
            }

            // Set tile to be colour.
            grid.get(coord1.getY())[coord1.getX()] = colourIdentifier;
            grid.get(coord2.getY())[coord2.getX()] = colourIdentifier;

            total_connected += 1;

            // Add connections to the path for that colour.
            paths.get(colourIdentifier).add(coord1);
            paths.get(colourIdentifier).add(coord2);
        }
    }


    /**
     * Draw a row of the puzzle.
     * @param idx - Integer for which row is being created.
     * @param row - Char[] where . is an empty cell and anything else is a colour starting node.
     */
    public void drawPuzzle(int idx, char[] row){
        grid.add(new char[w]);
        startGrid.add(row);
        for (int j = 0; j < row.length; j++){
            grid.get(idx)[j] = '.';
            if (row[j] != '.'){
                Coordinate coord = new Coordinate(idx, j, row[j]);
                if (!colourIdentifiers.contains(Integer.parseInt(String.valueOf(row[j])))) {
                    startColours.add(coord);
                    colourIdentifiers.add(Integer.parseInt(String.valueOf(row[j])));
                }
                allStartColours.add(coord);
            }
        }
    }

    /**
     * Check that all the nodes are connected and in a continuous manner.
     * @return Boolean - True if user has valid solution otherwise false.
     */
    public boolean checkWin(){
        int connected = 0;

        // Check that the connections are not more than 2
        boolean single_connectors = false;
        for (Coordinate coord : connections.keySet()){
            if (!allStartColours.contains(coord) && connections.get(coord).size() != 2){
                single_connectors = true;
                addErrorTiles(coord);
            }
        }
        if (single_connectors){return false;}

        // Check all are connected.
        for (Coordinate coordinate : startColours){
            char number = coordinate.getNumber();
            if (!paths.containsKey(number)){return false;}
            connected += paths.get(number).size();
        }
        return connected == h*w;
    }

    /**
     * Add the coordinates of a tile to the invalidTiles arrayList.
     * @param coord - Coordinate of invalid tile.
     */
    public void addErrorTiles(Coordinate coord){
        invalidTiles.add(coord);
    }


    /**
     * Check that the path between the two tiles if valid:
     *  # Not attempting to recolour a starting tile.
     *  # Not trying to redraw a path that is already made.
     *  # Not trying to connect more than one tile to a starting tile.
     *  # Not trying to connect more than 2 paths to a tile. (3 means not continuous)
     *  # Not trying to connect two different colours together.
     * @param y1 - Y-Coordinate of tile 1.
     * @param x1 - X-Coordinate of tile 1.
     * @param y2 - Y-Coordinate of tile 2.
     * @param x2 - X-Coordinate of tile2.
     * @param colourIdentifierNumber - Colour identifier as a number.
     * @return Boolean - True if valid connection between tiles otherwise False.
     */
    public boolean isValid(int y1, int x1, int y2, int x2, int colourIdentifierNumber){
        ArrayList<Coordinate> coords = new ArrayList<>();
        try{
            char colourIdentifier = (char) (48+colourIdentifierNumber);
            if (!paths.containsKey(colourIdentifier)){
                paths.put(colourIdentifier, new HashSet<>());
            }

            // Get coordinates in range provided and store.
            int vertical = Math.abs(y1 - y2);
            int horizontal = Math.abs(x1 - x2);

            // Get coords involved in the connection.
            Coordinate coord1;
            Coordinate coord2;
            for (int i = 0; i < (vertical + horizontal); i++) {
                if (vertical > 0) {
                    coords.add(new Coordinate(y1 + i, x1, grid.get(y1 + i)[x1]));
                    coords.add( new Coordinate(y1 + i + 1, x1, grid.get(y1 + i + 1)[x1]));
                } else {
                    coords.add( new Coordinate(y1, x1 + i, grid.get(y1)[x1 + i]));
                    coords.add( new Coordinate(y1, x1 + i + 1, grid.get(y1)[x1 + i + 1]));
                }
            }

            // Check that pairs are valid.
            for (int i = 0; i < coords.size(); i+=2){
                coord1 = coords.get(i);
                coord2 = coords.get(i+1);

                // Add the coordinates to the connections if not seen yet.
                if (!connections.containsKey(coord1)){connections.put(coord1, new HashSet<>());}
                if (!connections.containsKey(coord2)){connections.put(coord2, new HashSet<>());}

                // Ensure that a start node is not being converted to a new value.
                if (allStartColours.contains(coord1) && startGrid.get(coord1.getY())[coord1.getX()] != colourIdentifier ||
                        allStartColours.contains(coord2) && startGrid.get(coord2.getY())[coord2.getX()] != colourIdentifier){
                    throw new Exception("Can't connect a different colour to a starting node.");
                }

                // Connection already made
                if (connections.containsKey(coord1) && connections.get(coord1).contains(coord2)){
                    throw new Exception("All or part of the connection is already made.");
                }

                // Ensure only one connection out of a start node.
                if (allStartColours.contains(coord1) && connections.get(coord1).size() == 1 || allStartColours.contains(coord2) && connections.get(coord2).size() == 1){
                    throw new Exception("Can't connect more than two paths to a starting tile.");
                }

                // Two connections out of the tiles
                if (connections.get(coord1).size() == 2 || connections.get(coord2).size() == 2){
                    throw new Exception("Can't connect more than two paths to a tile.");
                }

                // Ensure connection matches the value.
                if (coord1.getNumber() != '.' && colourIdentifier != coord1.getNumber() ||
                        coord2.getNumber() != '.' && colourIdentifier != coord2.getNumber()){
                    throw new Exception("Can't connect two colours together.");
                }

                // Add connections between the tiles.
                // (Added here as it is valid and need to update to check for >1 path connections)
                connections.get(coord1).add(coord2);
                connections.get(coord2).add(coord1);

            }
            return true;
        }
        catch (Exception e) {
            // Invalid connection, add the invalid tiles, set the errorMessage and return false.
            invalidTiles.addAll(coords);
            Referee.errorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Check if the game is ended or not.
     * @return Boolean - True if the game is ended otherwise False.
     */
    public boolean isEnded(){
        return total_connected == h*w-startColours.size();
    }

    /**
    General getters below.
    */

    public int getHeight() {
        return h;
    }
    public int getWidth() {
        return w;
    }

    public int getUnconnected(){
        return invalidTiles.size();
    }

    public ArrayList<char[]> getGrid() {
        return grid;
    }

    public ArrayList<char[]> getStartGrid() {
        return startGrid;
    }

    public Set<Integer> getColourIdentifiers(){
        return colourIdentifiers;
    }

    public ArrayList<Coordinate> getErrorTiles(){
        return invalidTiles;
    }

    public Map<Coordinate, Set<Coordinate>> getConnections(){
        return connections;
    }
}
