package radiant.seven;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameMapIntegrationTest {

    @Test
    void testGameMapInstanceIsNotNull() {
        GameMap gameMap = GameMap.getInstance();
        assertThat(gameMap).isNotNull();
    }

    @Test
    void testGameMapSingleton() {
        GameMap instance1 = GameMap.getInstance();
        GameMap instance2 = GameMap.getInstance();
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void testGameMapHasPositiveWidth() {
        GameMap gameMap = GameMap.getInstance();
        assertThat(gameMap.getWidth()).isGreaterThan(0);
    }

    @Test
    void testGameMapHasPositiveHeight() {
        GameMap gameMap = GameMap.getInstance();
        assertThat(gameMap.getHeight()).isGreaterThan(0);
    }

    @Test
    void testRoadNetworkExists() {
        GameMap gameMap = GameMap.getInstance();
        assertThat(gameMap.getRoadNetwork()).isNotNull();
    }

    @Test
    void testRoadNetworkHasNodes() {
        GameMap gameMap = GameMap.getInstance();
        assertThat(gameMap.getRoadNetwork().size()).isGreaterThan(0);
    }
}