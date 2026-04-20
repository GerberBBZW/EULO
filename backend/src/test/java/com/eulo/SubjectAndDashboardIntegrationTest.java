package com.eulo;

import com.eulo.model.Session;
import com.eulo.model.Subject;
import com.eulo.model.VocationalGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstests für Subject-, VocationalGroup- und Dashboard-API.
 */
@DisplayName("Subject / VocationalGroup / Dashboard API")
class SubjectAndDashboardIntegrationTest extends TestBase {

    // -----------------------------------------------------------------------
    // GET /api/subjects — öffentlich
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/subjects")
    class GetSubjects {

        @Test
        @DisplayName("Öffentlicher Endpoint → Liste aller Fächer ohne Token")
        void getSubjects_withoutToken_returns200WithList() throws Exception {
            when(subjectRepository.findAll()).thenReturn(List.of(
                    new Subject("s1", "Web Development", "vocational", 5),
                    new Subject("s2", "Database Systems", "vocational", 3),
                    new Subject("s3", "Mathematics", "general", 8)
            ));

            mockMvc.perform(get("/api/subjects"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value("s1"))
                    .andExpect(jsonPath("$[0].name").value("Web Development"))
                    .andExpect(jsonPath("$[2].category").value("general"));
        }

        @Test
        @DisplayName("Kein Fach vorhanden → leere Liste (nicht 404)")
        void getSubjects_emptyDb_returnsEmptyList() throws Exception {
            when(subjectRepository.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/subjects"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/vocational-groups — öffentlich
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/vocational-groups")
    class GetVocationalGroups {

        @Test
        @DisplayName("Öffentlicher Endpoint → Liste der Berufsgruppen ohne Token")
        void getVocationalGroups_withoutToken_returns200() throws Exception {
            when(vocationalGroupRepository.findAll()).thenReturn(List.of(
                    new VocationalGroup("vg1", "Information Technology",
                            "Software, networks, databases", "💻", List.of()),
                    new VocationalGroup("vg2", "Business Administration",
                            "Finance, marketing", "📊", List.of())
            ));

            mockMvc.perform(get("/api/vocational-groups"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Information Technology"))
                    .andExpect(jsonPath("$[0].icon").value("💻"));
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/dashboard/stats
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/dashboard/stats")
    class GetDashboardStats {

        @Test
        @DisplayName("Mit Token → Stats-Objekt mit korrekten Werten")
        void getDashboardStats_withAuth_returnsStats() throws Exception {
            String token = obtainToken();

            String tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).toString();
            Session upcoming = new Session("s1", "u1", "Alex Rivera", "u2", "Sarah Chen",
                    "s1", "Web Development", "matched", tomorrow, "online", null);
            Session open = new Session("s2", "u1", "Alex Rivera", "u3", "Marcus",
                    "s3", "Mathematics", "open", tomorrow, "onsite", null);
            Session completed = new Session("s3", "u7", "Jenny", "u1", "Alex Rivera",
                    "s5", "Programming", "completed", tomorrow, "online", null);

            when(sessionRepository.findBySeekerIdOrTutorId("u1", "u1"))
                    .thenReturn(List.of(upcoming, open, completed));
            when(offerRepository.findAll()).thenReturn(List.of());
            when(subjectRepository.count()).thenReturn(5L);

            mockMvc.perform(get("/api/dashboard/stats").param("userId", "u1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    // 2 nicht-abgeschlossene Sessions (matched + open)
                    .andExpect(jsonPath("$.upcomingSessions").value(2))
                    .andExpect(jsonPath("$.subjectsOffered").value(5))
                    .andExpect(jsonPath("$.availableTutors").value(0))
                    // upcomingSession = erste "matched"-Session mit seekerId=u1
                    .andExpect(jsonPath("$.upcomingSession.id").value("s1"))
                    .andExpect(jsonPath("$.upcomingSession.status").value("matched"));
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void getDashboardStats_withoutToken_returns401() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats").param("userId", "u1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Benutzer ohne Sessions → upcomingSessions=0, upcomingSession=null")
        void getDashboardStats_noSessions_returnsZeroes() throws Exception {
            String token = obtainToken();
            when(sessionRepository.findBySeekerIdOrTutorId("u1", "u1")).thenReturn(List.of());
            when(offerRepository.findAll()).thenReturn(List.of());
            when(subjectRepository.count()).thenReturn(3L);

            mockMvc.perform(get("/api/dashboard/stats").param("userId", "u1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.upcomingSessions").value(0))
                    .andExpect(jsonPath("$.upcomingSession").doesNotExist());
        }
    }
}
