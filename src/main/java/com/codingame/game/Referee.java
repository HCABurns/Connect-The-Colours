package com.codingame.game;
import java.util.*;

import com.codingame.game.modules.Renderer;
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
    @Inject private Renderer module;

    // Define the required variables.
    private Board board;
    public static String errorMessage;

    @Override
    public void init() {

        // Set frame duration.
        gameManager.setFrameDuration(600);
        gameManager.setFirstTurnMaxTime(3000);
        gameManager.setMaxTurns(50);
        gameManager.setTurnMaxTime(50);

        // Get inputs from the case and send to user.
        Integer[] grid_dimensions = Arrays.stream(gameManager.getTestCaseInput().get(0).split(" "))
                .map(Integer::valueOf)
                .toArray(Integer[]::new);

        // Create board.
        board = new Board(grid_dimensions[0], grid_dimensions[1]);

        // Send the player the inputs
        gameManager.getPlayer().sendInputLine(board.getHeight() +" "+ board.getWidth());
        for (int i = 1; i < gameManager.getTestCaseInput().size(); i++){
            gameManager.getPlayer().sendInputLine(gameManager.getTestCaseInput().get(i));
        }

        // Send the puzzle to the Board to be created.
        for (int i = 1; i < board.getHeight()+1; i++) {
            char[] row = gameManager.getTestCaseInput().get(i).toCharArray();
            board.drawPuzzle(i - 1, row);
            for (int j = 0; j < row.length; j++){
                module.drawTile(row[j], i-1, j);
            }
        }
        // Scale the group to fit inside the frame - Also add to viewport for scrolling.
        module.scaleGroup(board.getWidth(), board.getHeight());
        module.getGroup().setZIndex(module.getZ_UI());

        // Add the group to the viewport.
        viewportModule.createViewport(module.getGroup());
    }

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
                module.drawConnector(y1, x1, y2, x2, number);

                // Check if the grid is valid.
                if (!board.isEmptyTiles()){
                    boolean valid = board.checkWin();
                    if (valid) {
                        module.addCompletedTiles(board);
                        gameManager.setFrameDuration(100*board.getHeight()* board.getWidth());
                        gameManager.winGame("Successfully connected all colours!");
                    }
                    else{
                        end("Not all connected...");}
                }
            }else{
                if (errorMessage == null){
                    errorMessage = "Not all lines are connected in a continuous manner.";
                }
                gameManager.setFrameDuration(100*board.getUnconnected());
                end(errorMessage);
            }
        }
        catch (TimeoutException e) {
            end("Timeout");
        }
    }

    public void end(String m){
        module.setErrorTiles(board);
        gameManager.loseGame(m);
    }


    public int[] checkOutputs(List<String> outputs){

        if (outputs.size() != 1){gameManager.loseGame("You did not send an output.");}
        String[] arr = outputs.get(0).split(" ");
        int[] values = new int[5];

        try{
            if (arr.length != 5){throw new Exception("Error: Incorrect number of inputs.");}
            // Convert values to the inputs.
            int x1 = Integer.parseInt(arr[0]);
            int y1 = Integer.parseInt(arr[1]);
            int x2 = Integer.parseInt(arr[2]);
            int y2 = Integer.parseInt(arr[3]);
            int number = Integer.parseInt(arr[4]);
            if ((y1+x1) < (y2+x2)) {
                values[0] = y1;values[1] = x1;values[2] = y2;values[3] = x2;
            }else{
                values[0] = y2;values[1] = x2;values[2] = y1;values[3] = x1;
            }
            values[4] = number;

            if (board.isValid(values[0], values[1], values[2],values[3], number)){
                return values;
            }
            return null;
        }
        catch (NumberFormatException e){
            errorMessage = "One or more of the inputs was not an integer.";
            return null;
        }
        catch (Exception e) {
            errorMessage = e.getMessage();
            return null;
        }

    }
}
