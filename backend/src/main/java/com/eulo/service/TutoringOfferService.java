package com.eulo.service;
 
import com.eulo.model.TutoringOffer;
import com.eulo.repository.TutoringOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class TutoringOfferService {
 
    private final TutoringOfferRepository offerRepository;
 
    public List<TutoringOffer> findAll(String search, String subjectName, String mode) {
        List<TutoringOffer> offers = offerRepository.findAll();
 
        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            offers = offers.stream()
                    .filter(o -> o.getTutorName().toLowerCase().contains(lower)
                            || o.getSubjectName().toLowerCase().contains(lower)
                            || o.getDescription().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }
 
        if (subjectName != null && !subjectName.isBlank() && !subjectName.equals("all")) {
            offers = offers.stream()
                    .filter(o -> o.getSubjectName().equals(subjectName))
                    .collect(Collectors.toList());
        }
 
        if (mode != null && !mode.isBlank() && !mode.equals("all")) {
            offers = offers.stream()
                    .filter(o -> o.getMode().equals(mode) || o.getMode().equals("both"))
                    .collect(Collectors.toList());
        }
 
        return offers;
    }
 
    public List<TutoringOffer> findByTutorId(String tutorId) {
        return offerRepository.findByTutorId(tutorId);
    }
 
    public TutoringOffer save(TutoringOffer offer) {
        return offerRepository.save(offer);
    }
 
    public void deleteById(String id) {
        offerRepository.deleteById(id);
    }
}
