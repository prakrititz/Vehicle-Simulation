package radiant.seven;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrafficManagerIntegrationTest {

    @Test
    void testTrafficManagerInstanceIsNotNull() {
        TrafficManager manager = TrafficManager.getInstance();
        assertThat(manager).isNotNull();
    }

    @Test
    void testTrafficManagerSingleton() {
        TrafficManager instance1 = TrafficManager.getInstance();
        TrafficManager instance2 = TrafficManager.getInstance();
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void testTrafficLightsListIsNotNull() {
        assertThat(TrafficManager.trafficLights).isNotNull();
    }

    @Test
    void testChangeSignalsExecutesSuccessfully() {
        try {
            TrafficManager.changeSignals();
            assertThat(true).isTrue(); // If no exception, test passes
        } catch (Exception e) {
            assertThat(false).isTrue(); // Should not reach here
        }
    }
}