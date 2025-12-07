package radiant.seven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class TerminalSimulationTest {

    private GameMap gameMap;
    private TrafficManager trafficManager;

    @BeforeEach
    void setUp() {
        gameMap = GameMap.getInstance();
        trafficManager = TrafficManager.getInstance();
        
        // Clear EV map before each test
        if (EVController.evMap != null) {
            EVController.evMap.clear();
        }
    }

    @Test
    void testGameMapInitialization() {
        assertThat(gameMap).isNotNull();
        assertThat(gameMap.getWidth()).isGreaterThan(0);
        assertThat(gameMap.getHeight()).isGreaterThan(0);
    }

    @Test
    void testTrafficManagerInitialization() {
        assertThat(trafficManager).isNotNull();
        assertThat(TrafficManager.trafficLights).isNotNull();
    }

    @Test
    void testEVCreationAndConfiguration() {
        EV ev1 = new EV(4, 35, 1, 100, 10);
        ev1.setName("EV1");
        ev1.setEndLocation(35, 2);

        assertThat(ev1).isNotNull();
        assertThat(ev1.getName()).isEqualTo("EV1");
        assertThat(ev1.getStartX()).isEqualTo(4);
        assertThat(ev1.getStartY()).isEqualTo(35);
        assertThat(ev1.getEndX()).isEqualTo(35);
        assertThat(ev1.getEndY()).isEqualTo(2);
        assertThat(ev1.getCharge()).isEqualTo(100);
    }

    @Test
    void testMultipleEVCreation() {
        EV ev1 = new EV(4, 35, 1, 100, 10);
        ev1.setName("EV1");
        ev1.setEndLocation(35, 2);

        EV ev2 = new EV(2, 35, 2, 100, 10);
        ev2.setName("EV2");
        ev2.setEndLocation(35, 2);

        EVController.evMap.put("EV1", ev1);
        EVController.evMap.put("EV2", ev2);

        assertThat(EVController.evMap.size()).isEqualTo(2);
        assertThat(EVController.evMap.containsKey("EV1")).isTrue();
        assertThat(EVController.evMap.containsKey("EV2")).isTrue();
    }

    @Test
    void testPathfindingVisualizerCreation() {
        PathfindingVisualizer pathfinder = new PathfindingVisualizer(gameMap);
        assertThat(pathfinder).isNotNull();
    }

    @Test
    void testPathGeneration() {
        EV ev = new EV(4, 35, 1, 100, 10);
        ev.setName("TestEV");
        ev.setEndLocation(35, 2);

        PathfindingVisualizer pathfinder = new PathfindingVisualizer(gameMap);
        long[] pathArray = pathfinder.findPath(
                ev.getStartX(),
                ev.getStartY(),
                ev.getEndX(),
                ev.getEndY());

        assertThat(pathArray).isNotNull();
        assertThat(pathArray.length).isGreaterThan(0);
    }

    @Test
    void testConvertToPathNodes() {
        long[] pathArray = {5, 10, 15, 20, 25, 30};
        List<PathNode> nodes = convertToPathNodes(pathArray);

        assertThat(nodes).isNotNull();
        assertThat(nodes.size()).isEqualTo(3);
        assertThat(nodes.get(0).getX()).isEqualTo(5);
        assertThat(nodes.get(0).getY()).isEqualTo(10);
    }

    @Test
    void testEVPathSetting() {
        EV ev = new EV(4, 35, 1, 100, 10);
        ev.setName("TestEV");
        
        List<PathNode> path = new ArrayList<>();
        path.add(new PathNode(4, 35));
        path.add(new PathNode(5, 35));
        path.add(new PathNode(6, 35));
        
        ev.setPath(path);

        assertThat(ev.getPath()).isNotNull();
        assertThat(ev.getPath().size()).isEqualTo(3);
    }

    @Test
    void testEVMovingState() {
        EV ev = new EV(4, 35, 1, 100, 10);
        ev.setName("TestEV");
        
        assertThat(ev.isMoving()).isFalse();
        
        ev.setMoving(true);
        assertThat(ev.isMoving()).isTrue();
        
        ev.setMoving(false);
        assertThat(ev.isMoving()).isFalse();
    }

    @Test
    void testMapWalkability() {
        // Test that the map has walkable nodes
        boolean foundWalkable = false;
        for (int i = 1; i <= gameMap.getHeight() && !foundWalkable; i++) {
            for (int j = 1; j <= gameMap.getWidth() && !foundWalkable; j++) {
                if (gameMap.isWalkable(i, j)) {
                    foundWalkable = true;
                }
            }
        }
        assertThat(foundWalkable).isTrue();
    }

    @Test
    void testTrafficNodeRetrieval() {
        // Test that traffic nodes can be retrieved
        assertThat(TrafficManager.trafficLights).isNotNull();
        
        if (!TrafficManager.trafficLights.isEmpty()) {
            TrafficNode firstTrafficNode = TrafficManager.trafficLights.get(0);
            TrafficNode retrieved = gameMap.getTrafficNode(firstTrafficNode.x, firstTrafficNode.y);
            assertThat(retrieved).isNotNull();
        }
    }

    @Test
    void testEVMapOperations() {
        EVController.evMap.clear();
        
        EV ev = new EV(4, 35, 1, 100, 10);
        ev.setName("TestEV");
        
        EVController.evMap.put("TestEV", ev);
        assertThat(EVController.evMap.size()).isEqualTo(1);
        
        EV retrieved = EVController.evMap.get("TestEV");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("TestEV");
        
        EVController.evMap.remove("TestEV");
        assertThat(EVController.evMap.size()).isEqualTo(0);
    }

    // Helper method to convert path array to PathNodes
    private List<PathNode> convertToPathNodes(long[] path) {
        List<PathNode> nodes = new ArrayList<>();
        int maxDim = gameMap.getWidth();

        for (int i = 0; i < path.length; i += 2) {
            int x = Math.min(Math.max((int) path[i], 1), maxDim);
            int y = Math.min(Math.max((int) path[i + 1], 1), maxDim);
            nodes.add(new PathNode(x, y));
        }
        return nodes;
    }
}