package com.eulo;

import com.eulo.model.TutoringOffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstests für POST /api/offers — Angebote erstellen, filtern und löschen.
 */
@DisplayName("TutoringOffer API")
class OfferIntegrationTest extends TestBase {

    private TutoringOffer buildOffer(String id) {
        return new TutoringOffer(id, "u1", "Alex Rivera", null,
                "s1", "Web Development", "online",
                "Happy to help with HTML, CSS and JS.", "Mon, Wed • After school");
    }

    // -----------------------------------------------------------------------
    // GET /api/offers — öffentlich
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/offers")
    class GetOffers {

        @Test
        @DisplayName("Ohne Token erreichbar → 200 mit Angebots-Liste")
        void getOffers_publicEndpoint_returns200WithList() throws Exception {
            when(offerRepository.findAll()).thenReturn(List.of(buildOffer("o1"), buildOffer("o2")));

            mockMvc.perform(get("/api/offers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value("o1"))
                    .andExpect(jsonPath("$[0].subjectName").value("Web Development"))
                    .andExpect(jsonPath("$[1].id").value("o2"));
        }

        @Test
        @DisplayName("Suchfilter 'Web' → nur passende Angebote")
        void getOffers_withSearchFilter_returnsFilteredList() throws Exception {
            var webOffer  = buildOffer("o1");
            var mathOffer = new TutoringOffer("o2", "u3", "Marcus", null,
                    "s3", "Mathematics", "onsite", "Algebra help.", "Tue");
            when(offerRepository.findAll()).thenReturn(List.of(webOffer, mathOffer));

            mockMvc.perform(get("/api/offers").param("search", "web"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].subjectName").value("Web Development"));
        }

        @Test
        @DisplayName("Modus-Filter 'online' schließt 'onsite'-Angebote aus")
        void getOffers_withModeFilter_excludesWrongMode() throws Exception {
            var online  = buildOffer("o1"); // mode = "online"
            var onsite  = new TutoringOffer("o2", "u3", "Marcus", null,
                    "s3", "Mathematics", "onsite", "Algebra.", "Tue");
            when(offerRepository.findAll()).thenReturn(List.of(online, onsite));

            mockMvc.perform(get("/api/offers").param("mode", "online"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].mode").value("online"));
        }

        @Test
        @DisplayName("Keine Angebote vorhanden → leere Liste (nicht 404)")
        void getOffers_emptyDb_returnsEmptyList() throws Exception {
            when(offerRepository.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/offers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/offers/tutor/{id}
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/offers/tutor/{tutorId}")
    class GetOffersByTutor {

        @Test
        @DisplayName("Mit gültigem Token → eigene Angebote werden zurückgegeben")
        void getOffersByTutor_withAuth_returnsOwnOffers() throws Exception {
            String token = obtainToken();
            when(offerRepository.findByTutorId("u1")).thenReturn(List.of(buildOffer("o6"), buildOffer("o7")));

            mockMvc.perform(get("/api/offers/tutor/u1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void getOffersByTutor_withoutAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/offers/tutor/u1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/offers
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/offers")
    class CreateOffer {

        @Test
        @DisplayName("Authentifiziert → Angebot wird erstellt und zurückgegeben")
        void createOffer_withAuth_returns200AndOffer() throws Exception {
            String token  = obtainToken();
            TutoringOffer saved = buildOffer("o_new");
            when(offerRepository.save(any())).thenReturn(saved);

            String body = """
                    {
                      "tutorId":"u1","tutorName":"Alex Rivera",
                      "subjectId":"s1","subjectName":"Web Development",
                      "mode":"online",
                      "availability":"Mon, Wed",
                      "description":"HTML & CSS basics"
                    }
                    """;

            mockMvc.perform(post("/api/offers")
                            .contentType(APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("o_new"))
                    .andExpect(jsonPath("$.subjectName").value("Web Development"));
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void createOffer_withoutAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/offers")
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // -----------------------------------------------------------------------
    // DELETE /api/offers/{id}
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("DELETE /api/offers/{id}")
    class DeleteOffer {

        @Test
        @DisplayName("Authentifiziert und vorhanden → 204 No Content")
        void deleteOffer_withAuth_returns204() throws Exception {
            String token = obtainToken();
            // Controller now fetches the offer first to verify ownership (IDOR guard)
            when(offerRepository.findById("o1")).thenReturn(Optional.of(buildOffer("o1")));
            doNothing().when(offerRepository).deleteById("o1");

            mockMvc.perform(delete("/api/offers/o1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Anderer Benutzer versucht Angebot zu löschen → 403 Forbidden (IDOR-Guard)")
        void deleteOffer_byDifferentUser_returns403() throws Exception {
            String token = obtainToken(); // u1
            // Offer belongs to u99 (not u1)
            TutoringOffer foreignOffer = new TutoringOffer("o99", "u99", "Other User", null,
                    "s1", "Math", "online", "Some desc.", "Mon");
            when(offerRepository.findById("o99")).thenReturn(Optional.of(foreignOffer));

            mockMvc.perform(delete("/api/offers/o99")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void deleteOffer_withoutAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/offers/o1"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
