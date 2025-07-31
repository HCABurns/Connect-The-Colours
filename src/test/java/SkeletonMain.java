import com.codingame.gameengine.runner.SoloGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        // Create solo game runner for local testing.
        SoloGameRunner gameRunner = new SoloGameRunner();

        // Sets the player
        gameRunner.setAgent(Agent1.class);
        //gameRunner.setAgent(Agent2.class);

        // Sets a test case
        gameRunner.setTestCase("test21.json"); // Intro (17 for agent1)

        // Starts the game.
        gameRunner.start();
    }
}
