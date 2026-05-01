package com.eulo.controller;

import com.eulo.model.TutoringOffer;
import com.eulo.security.InputSanitizer;
import com.eulo.service.TutoringOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class TutoringOfferController {

    private final TutoringOfferService offerService;

    @GetMapping
    public List<TutoringOffer> getOffers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String mode) {
        return offerService.findAll(search, subject, mode);
    }

    @GetMapping("/tutor/{tutorId}")
    public List<TutoringOffer> getOffersByTutor(@PathVariable String tutorId) {
        return offerService.findByTutorId(tutorId);
    }

    /**
     * Force tutorId to the authenticated user (prevents IDOR spoofing).
     * Sanitize free-text description against stored XSS.
     */
    @PostMapping
    public TutoringOffer createOffer(@Valid @RequestBody TutoringOffer offer, Authentication auth) {
        String me = (String) auth.getPrincipal();
        offer.setTutorId(me);
        offer.setDescription(InputSanitizer.sanitize(offer.getDescription()));
        return offerService.save(offer);
    }

    /**
     * IDOR guard: only the owner (tutor) of an offer may delete it.
     * Returns 404 if not found, 403 if authenticated user is not the owner.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable String id, Authentication auth) {
        String me = (String) auth.getPrincipal();

        Optional<TutoringOffer> found = offerService.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!me.equals(found.get().getTutorId())) {
            return ResponseEntity.status(403).build();
        }
        offerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
