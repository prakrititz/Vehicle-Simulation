package radiant.seven;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PathControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetMapDataEndpoint() throws Exception {
        mockMvc.perform(get("/api/map"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roadNetwork").exists());
    }

    @Test
    void testFindPathEndpoint() throws Exception {
        String requestBody = """
                {
                    "startX": 1,
                    "startY": 1,
                    "endX": 5,
                    "endY": 5
                }
                """;

        mockMvc.perform(post("/api/findPath")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testFindPathReturnsArray() throws Exception {
        String requestBody = """
                {
                    "startX": 2,
                    "startY": 2,
                    "endX": 8,
                    "endY": 8
                }
                """;

        mockMvc.perform(post("/api/findPath")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}