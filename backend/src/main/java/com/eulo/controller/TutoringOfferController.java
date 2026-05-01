package com.eulo.controller;

import com.eulo.model.TutoringOffer;
import com.eulo.service.TutoringOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public TutoringOffer createOffer(@Valid @RequestBody TutoringOffer offer) {
        return offerService.save(offer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable String id) {
        offerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
