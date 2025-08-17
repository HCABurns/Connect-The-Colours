package com.codingame.game;
import java.util.*;

import com.codingame.game.modules.Renderer;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.gameengine.module.viewport.ViewportModule;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    @Inject private ViewportModule viewportModule;
    @Inject private Renderer renderer;
    @Inject private TooltipModule tooltips;

    // Define the required variables.
    private Board board;
    private Board debug_board;
    public static String errorMessage;

    /**
     * Function to set up the board and visuals.
     */
    @Override
    public void init() {

        // Set frame duration.
        gameManager.setFrameDuration(600);
        gameManager.setFirstTurnMaxTime(2000);
        gameManager.setMaxTurns(65);
        gameManager.setTurnMaxTime(50);

        // Get inputs from the case and send to user.
        Integer[] grid_dimensions = Arrays.stream(gameManager.getTestCaseInput().get(0).split(" "))
                .map(Integer::valueOf)
                .toArray(Integer[]::new);

        // Create board.
        board = new Board(grid_dimensions[0], grid_dimensions[1]);
        debug_board = new Board(grid_dimensions[0], grid_dimensions[1]);

        // Send the player the inputs
        gameManager.getPlayer().sendInputLine(board.getHeight() +" "+ board.getWidth());
        for (int i = 1; i < gameManager.getTestCaseInput().size(); i++){
            gameManager.getPlayer().sendInputLine(gameManager.getTestCaseInput().get(i));
        }

        // Send the puzzle to the Board to be created.
        for (int i = 1; i < board.getHeight()+1; i++) {
            char[] row = gameManager.getTestCaseInput().get(i).toCharArray();
            board.drawPuzzle(i - 1, row);
            debug_board.drawPuzzle(i-1, row);
            for (int j = 0; j < row.length; j++){
                renderer.drawTile(row[j], i-1, j);
            }
        }

        // Scale the group to fit inside the frame - Also add to viewport for scrolling.
        renderer.scaleGroup(board.getWidth(), board.getHeight());
        renderer.getGroup().setZIndex(renderer.getZ_UI());

        // Hide the debug group
        renderer.getDebugGroup().setVisible(false);

        // Add the group to the viewport.
        viewportModule.createViewport(renderer.getGroup());

    }

    /**
     * Game turn function - Receives the users inputs, processes them and carries out required functions depending on
     * if the users inputs are valid or not. Additionally deals with ending the game.
     * @param turn - Integer for the users current turn.
     */
    @Override
    public void gameTurn(int turn) {
        gameManager.getPlayer().execute();

        try {
            // Check validity of the player output.
            List<String> outputs = gameManager.getPlayer().getOutputs();
            int[] out = checkOutputs(outputs);
            if (out != null){
                // Unpack the user response.
                int y1 = out[0];
                int x1 = out[1];
                int y2 = out[2];
                int x2 = out[3];
                char number = (char) (out[4] + 48);

                // Draw the connection(s) that the user has provided.
                board.addConnections(y1, x1, y2, x2, number);
                //renderer.drawConnector(y1, x1, y2, x2, number);

                // Check if the grid is valid.
                if (board.isEnded()){
                    // If all connections have been made, check that the board is valid.
                    boolean valid = board.checkWin();
                    if (valid) {
                        // Set visuals to completed and set the game result to win.
                        renderer.addCompletedTiles();
                        gameManager.setFrameDuration(50*board.getHeight()* board.getWidth());
                        gameManager.winGame("Successfully connected all colours!");
                    }
                    else{
                        // User has not won - Call the end function.
                        end("Not all lines are connected in a continuous manner.");}
                }
            }else{
                // User has not won - Call the end function.
                if (errorMessage == null){
                    errorMessage = "Not all lines are connected in a continuous manner.";
                }
                gameManager.setFrameDuration(200 + 200*(board.getUnconnected()));
                end(errorMessage);
            }
        }
        catch (TimeoutException e) {
            // User didn't provide an output in the allotted time, call the function to end the game.
            end("Timeout...");
        }
    }


    /**
     * Function to end the game.
     * @param m - ErrorMessage to display to the user.
     */
    public void end(String m){
        renderer.setErrorTiles(board);
        gameManager.loseGame(m);
    }


    /**
     * Check the inputs given by the user are valid.
     * @param outputs List of strings given by the user for this turn.
     * @return - Custom Sorted int[] of the users inputs if valid OTHERWISE set errorMessage and return null;
     */
    public int[] checkOutputs(List<String> outputs){

        if (outputs.size() != 1){gameManager.loseGame("You did not send a single input.");}
        String[] arr = outputs.get(0).split(" ");
        int[] values = new int[5];

        try{
            if (arr.length != 5){throw new Exception("Incorrect number of inputs provided.");}
            // Convert values to the inputs.
            int x1 = Integer.parseInt(arr[0]);
            int y1 = Integer.parseInt(arr[1]);
            int x2 = Integer.parseInt(arr[2]);
            int y2 = Integer.parseInt(arr[3]);
            int number = Integer.parseInt(arr[4]);
            // Set y1,x1 to be the left-most coordinate.
            if ((y1+x1) < (y2+x2)) {
                values[0] = y1;values[1] = x1;values[2] = y2;values[3] = x2;
            }else{
                values[0] = y2;values[1] = x2;values[2] = y1;values[3] = x1;
            }
            values[4] = number;

            // Complete general checks on the input. (Bounds and Valid colour)
            generalChecks(values[0], values[1], values[2],values[3], number);

            // Valid input provided so render regardless if the moves are valid or not - Better for debugging.
            renderer.drawConnector(values[0], values[1], values[2], values[3], (char)(48+number), board.getConnections());

            // Check if the move is actually valid given the rules.
            if (board.isValid(values[0], values[1], values[2],values[3], number)){
                return values;
            }
            return null;
        }
        catch (NumberFormatException e){
            String[] error = e.getMessage().split(" ");
            errorMessage = "One or more of the inputs was invalid: " + error[error.length-1];
            return null;
        }
        catch (Exception e) {
            errorMessage = e.getMessage();
            return null;
        }
    }

    /**
     * Perform general checks on the data to ensure that they are in the correct format:
     *  # The colour identifier is valid for the given puzzle.
     *  # Not out of bounds.
     *  # Not the same tile and (x1==x2 or y1==y2)
     * @param y1 - Y-Coordinate of tile 1.
     * @param x1 - X-Coordinate of tile 1.
     * @param y2 - Y-Coordinate of tile 2.
     * @param x2 - X-Coordinate of tile2.
     * @param number - Colour identifier number.
     * @throws Exception - Throws exception if not in the correct format.
     */
    public void generalChecks(int y1, int x1, int y2, int x2, int number) throws Exception{
        // NOTE: These checks are general checks for correctness - Additional checks in Board.isValid().
        // Check for valid number.
        if (!board.getColourIdentifiers().contains(number)){
            throw new Exception("Invalid colour identifier provided.");
        }

        // Check for bounds of provided coordinates.
        if (y1 < 0 || y1 >= board.getHeight() || y2 < 0 || y2 >= board.getHeight() || x1 < 0 || x1 >= board.getWidth() || x2 < 0 || x2 >= board.getWidth()){
            throw new Exception("One or both of the inputs is out of bounds.");
        }

        // Check in straight line or same input.
        int vertical = Math.abs(y1 - y2);
        int horizontal = Math.abs(x1 - x2);
        if ((vertical == 0 && horizontal == 0)){
            board.addErrorTiles(new Coordinate(y1,x1));
            throw new Exception("Same tile or not straight line provided.");
        }else if ((vertical > 0 && horizontal > 0)){
            board.addErrorTiles(new Coordinate(y1, x1));
            board.addErrorTiles(new Coordinate(y2, x2));
            throw new Exception("Same tile or not straight line provided.");
        }
    }
}
