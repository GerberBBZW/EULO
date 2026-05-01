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
     * Force tutorId to the authenticated user (prevents spoofing).
     * Sanitize free-text description.
     */
    @PostMapping
    public TutoringOffer createOffer(@Valid @RequestBody TutoringOffer offer, Authentication auth) {
        String me = (String) auth.getPrincipal();
        offer.setTutorId(me);                                                   // IDOR: cannot post as someone else
        offer.setDescription(InputSanitizer.sanitize(offer.getDescription()));   // XSS sanitize
        return offerService.save(offer);
    }

    /**
     * IDOR guard: only the owner (tutor) of an offer may delete it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable String id, Authentication auth) {
        String me = (String) auth.getPrincipal();
        return offerService.findById(id)
                .map(offer -> {
                    if (!me.equals(offer.getTutorId())) {
                        return ResponseEntity.status(403).<Void>build();
                    }
                    offerService.deleteById(id);
                    return ResponseEntity.<Void>noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
