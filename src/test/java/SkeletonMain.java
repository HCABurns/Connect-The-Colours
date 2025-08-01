import com.codingame.gameengine.runner.SoloGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        // Create solo game runner for local testing.
        SoloGameRunner gameRunner = new SoloGameRunner();

        // Sets the player
        gameRunner.setAgent(Agent1.class);
        gameRunner.setAgent(Agent2.class);

        // Sets a test case
        //gameRunner.setTestCase("test21.json"); // Intro (17 for agent1)
        gameRunner.setTestCase("test9.json"); // medium 1
        //gameRunner.setTestCase("test32.json"); // sparse 1
        //gameRunner.setTestCase("test27.json"); // single

        // Starts the game.
        gameRunner.start();
    }
}
