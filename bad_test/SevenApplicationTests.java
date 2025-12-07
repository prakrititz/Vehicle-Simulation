package radiant.seven;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SevenApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void applicationContextHasBeans() {
        assertThat(applicationContext.getBeanDefinitionCount()).isGreaterThan(0);
    }

    @Test
    void applicationHasRequiredBeans() {
        assertThat(applicationContext.containsBean("EVController")).isTrue();
    }
}