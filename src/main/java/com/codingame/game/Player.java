package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.core.AbstractSoloPlayer;

// Uncomment the line below and comment the line under it to create a Solo Game
public class Player extends AbstractSoloPlayer {
//public class Player extends AbstractMultiplayerPlayer {
    @Override
    public int getExpectedOutputLines() {
        // Returns the number of expected lines of outputs for a player (DONE)
        return 1;
    }
}
