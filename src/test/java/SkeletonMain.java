import com.codingame.gameengine.runner.SoloGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        // Create solo game runner for local testing.
        SoloGameRunner gameRunner = new SoloGameRunner();

        // Sets the player
        gameRunner.setAgent(Agent1.class);
        //gameRunner.setAgent(Agent2.class);

        // Sets a test case
        gameRunner.setTestCase("test17.json");

        // Starts the game.
        gameRunner.start();
    }
}
