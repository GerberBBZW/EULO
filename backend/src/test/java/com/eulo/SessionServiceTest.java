package com.eulo;

import com.eulo.model.Session;
import com.eulo.repository.SessionRepository;
import com.eulo.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für SessionService — Buchungs-Logik und Status-Übergänge.
 */
@DisplayName("SessionService Unit Tests")
@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session openSession;

    @BeforeEach
    void setUp() {
        openSession = new Session("s1", "u1", "Alex", "u2", "Sarah",
                "sub1", "Mathematics", "open", "2026-06-01T10:00:00Z", "online", null);
    }

    @Test
    @DisplayName("save → Session wird gespeichert und zurückgegeben")
    void save_persistsAndReturnsSession() {
        when(sessionRepository.save(any())).thenReturn(openSession);

        Session result = sessionService.save(openSession);

        assertThat(result.getId()).isEqualTo("s1");
        assertThat(result.getStatus()).isEqualTo("open");
        verify(sessionRepository, times(1)).save(openSession);
    }

    @Test
    @DisplayName("updateStatus → Status wird korrekt geändert")
    void updateStatus_changesStatusAndSaves() {
        Session matched = new Session("s1", "u1", "Alex", "u2", "Sarah",
                "sub1", "Mathematics", "matched", "2026-06-01T10:00:00Z", "online", null);
        when(sessionRepository.findById("s1")).thenReturn(Optional.of(openSession));
        when(sessionRepository.save(any())).thenReturn(matched);

        Optional<Session> result = sessionService.updateStatus("s1", "matched");

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("matched");
    }

    @Test
    @DisplayName("updateStatus → unbekannte ID gibt leer zurück")
    void updateStatus_unknownId_returnsEmpty() {
        when(sessionRepository.findById("unknown")).thenReturn(Optional.empty());

        Optional<Session> result = sessionService.updateStatus("unknown", "matched");

        assertThat(result).isEmpty();
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("findByUserId → gibt Sessions für Seeker und Tutor zurück")
    void findByUserId_returnsBothSeekerAndTutorSessions() {
        when(sessionRepository.findBySeekerIdOrTutorId("u1", "u1"))
                .thenReturn(List.of(openSession));

        List<Session> result = sessionService.findByUserId("u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeekerId()).isEqualTo("u1");
    }
}
