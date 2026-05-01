package com.eulo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Smoke-Tests für den Health-Endpoint und öffentliche Erreichbarkeit (D4.9 SM-01/SM-02).
 */
@DisplayName("Actuator / Health-Endpoint (Smoke Tests)")
class ActuatorAndHealthTest extends TestBase {

    @Test
    @DisplayName("GET /actuator/health → 200 + status UP (SM-01)")
    void actuatorHealth_returns200AndUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("GET /actuator/health ohne Token → öffentlich erreichbar")
    void actuatorHealth_withoutToken_isPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /actuator/health gibt keinen Stack Trace zurück")
    void actuatorHealth_doesNotExposeInternals() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details").doesNotExist());
    }
}
