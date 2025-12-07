package radiant.seven;

import org.junit.jupiter.api.BeforeEach;
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
class EVControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        if (EVController.evMap != null) {
            EVController.evMap.clear();
        }
    }

    @Test
    void testGetAllEVsEndpoint() throws Exception {
        mockMvc.perform(get("/api/ev/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTrafficSignalsEndpoint() throws Exception {
        mockMvc.perform(get("/api/ev/traffic/signals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testChangeTrafficSignalsEndpoint() throws Exception {
        mockMvc.perform(post("/api/ev/traffic/change"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetNonExistentEVReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/ev/NonExistentEV/status"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistentEVReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/ev/NonExistentEV"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEVAndVerifyStatus() throws Exception {
        String requestBody = """
                {
                    "name": "TestEV1",
                    "startX": 5,
                    "startY": 5,
                    "endX": 10,
                    "endY": 10,
                    "type": 1,
                    "charge": 100,
                    "chargingRate": 5,
                    "vehicleType": "sedan"
                }
                """;

        mockMvc.perform(post("/api/ev/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestEV1"))
                .andExpect(jsonPath("$.charge").value(100));
    }

    @Test
    void testCreateAndDeleteEV() throws Exception {
        String requestBody = """
                {
                    "name": "TestEV2",
                    "startX": 3,
                    "startY": 3,
                    "endX": 8,
                    "endY": 8,
                    "type": 1,
                    "charge": 90,
                    "chargingRate": 5,
                    "vehicleType": "coupe"
                }
                """;

        mockMvc.perform(post("/api/ev/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/ev/TestEV2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/ev/TestEV2/status"))
                .andExpect(status().isNotFound());
    }
}