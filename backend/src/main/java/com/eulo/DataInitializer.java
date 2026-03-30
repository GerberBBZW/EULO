package com.eulo;
 
import com.eulo.model.*;
import com.eulo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
 
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
 
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
 
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final VocationalGroupRepository vocationalGroupRepository;
    private final TutoringOfferRepository offerRepository;
    private final SessionRepository sessionRepository;
 
    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // already seeded
        }
 
        // --- Subjects ---
        Subject webDev = new Subject("s1", "Web Development", "vocational", 5);
        Subject dbSystems = new Subject("s2", "Database Systems", "vocational", 3);
        Subject math = new Subject("s3", "Mathematics", "general", 8);
        Subject businessEnglish = new Subject("s4", "Business English", "general", 4);
        Subject introProg = new Subject("s5", "Intro to Programming", "vocational", 6);
        subjectRepository.saveAll(List.of(webDev, dbSystems, math, businessEnglish, introProg));
 
        // --- Vocational Groups ---
        VocationalGroup it = new VocationalGroup("vg1", "Information Technology",
                "Software, networks, databases and IT systems", "💻",
                List.of(webDev, dbSystems, introProg));
        VocationalGroup business = new VocationalGroup("vg2", "Business Administration",
                "Finance, marketing, management and commerce", "📊",
                List.of(math, businessEnglish));
        VocationalGroup healthcare = new VocationalGroup("vg3", "Healthcare",
                "Nursing, medicine, therapy and patient care", "🩺",
                List.of(math));
        VocationalGroup engineering = new VocationalGroup("vg4", "Engineering",
                "Mechanical, civil, electrical and chemical engineering", "⚙️",
                List.of(math, dbSystems));
        vocationalGroupRepository.saveAll(List.of(it, business, healthcare, engineering));
 
        // --- Users ---
        User alex = new User("u1", "Alex Rivera", "alex.rivera@school.edu", "password123",
                "student", "Information Technology",
                "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                "IT student passionate about coding and math. Happy to help with programming basics!",
                List.of("Web Development", "Mathematics"), 12);
 
        User sarah = new User("u2", "Sarah Chen", "sarah.chen@school.edu", "password123",
                "teacher", "Information Technology",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80",
                "Experienced tutor specializing in React and TypeScript.",
                List.of("Web Development"), 24);
 
        User marcus = new User("u3", "Marcus Johnson", "marcus.johnson@school.edu", "password123",
                "teacher", "Business Administration",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80",
                "Patient math tutor. I use visual methods to explain complex problems.",
                List.of("Mathematics"), 18);
 
        User emily = new User("u4", "Emily Davis", "emily.davis@school.edu", "password123",
                "teacher", "Business Administration", null,
                "Help with presentations, essay writing, and professional communication.",
                List.of("Business English"), 9);
 
        User david = new User("u5", "David Kim", "david.kim@school.edu", "password123",
                "teacher", "Information Technology",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80",
                "SQL expert. Can help with database design, normalization, and query optimization.",
                List.of("Database Systems"), 15);
 
        User jessica = new User("u6", "Jessica Wong", "jessica.wong@school.edu", "password123",
                "teacher", "Information Technology", null,
                "Frontend basics: HTML, CSS, JavaScript. Great for beginners!",
                List.of("Web Development"), 7);
 
        User jenny = new User("u7", "Jenny Wilson", "jenny.wilson@school.edu", "password123",
                "student", "Information Technology", null,
                "First-year IT student looking for help with programming basics.", null, 0);
 
        userRepository.saveAll(List.of(alex, sarah, marcus, emily, david, jessica, jenny));
 
        // --- Tutoring Offers ---
        TutoringOffer o1 = new TutoringOffer("o1", "u2", "Sarah Chen",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80",
                "s1", "Web Development", "online",
                "Specializing in React, TypeScript, and modern CSS. I can help you debug your code or understand core concepts.",
                "Mon, Wed, Fri • After 4PM");
 
        TutoringOffer o2 = new TutoringOffer("o2", "u3", "Marcus Johnson",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80",
                "s3", "Mathematics", "both",
                "Patient math tutor for Algebra and Calculus. I use visual methods to explain complex problems.",
                "Tue, Thu • Lunch break");
 
        TutoringOffer o3 = new TutoringOffer("o3", "u4", "Emily Davis", null,
                "s4", "Business English", "onsite",
                "Help with presentations, essay writing, and professional communication skills.",
                "Weekdays • Library");
 
        TutoringOffer o4 = new TutoringOffer("o4", "u5", "David Kim",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80",
                "s2", "Database Systems", "online",
                "SQL expert. Can help with database design, normalization, and query optimization.",
                "Weekends • Flexible");
 
        TutoringOffer o5 = new TutoringOffer("o5", "u6", "Jessica Wong", null,
                "s1", "Web Development", "both",
                "Frontend basics: HTML, CSS, JavaScript. Great for beginners!",
                "Mon-Wed • Evenings");
 
        TutoringOffer o6 = new TutoringOffer("o6", "u1", "Alex Rivera",
                "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                "s1", "Web Development", "online",
                "Happy to help with HTML, CSS and JavaScript basics.",
                "Mon, Wed • After school");
 
        TutoringOffer o7 = new TutoringOffer("o7", "u1", "Alex Rivera",
                "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                "s3", "Mathematics", "onsite",
                "Can help with algebra and basic calculus.",
                "Tue, Thu • Lunch");
 
        offerRepository.saveAll(List.of(o1, o2, o3, o4, o5, o6, o7));
 
        // --- Sessions ---
        String tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        String twoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS).toString();
        String threeDaysAhead = Instant.now().plus(3, ChronoUnit.DAYS).toString();
 
        Session s1 = new Session("sess1", "u1", "Alex Rivera", "u2", "Sarah Chen",
                "s1", "Web Development", "matched", tomorrow, "online", null);
 
        Session s2 = new Session("sess2", "u1", "Alex Rivera", "u3", "Marcus Johnson",
                "s3", "Mathematics", "completed", twoDaysAgo, "onsite", null);
 
        Session s3 = new Session("sess3", "u7", "Jenny Wilson", "u1", "Alex Rivera",
                "s5", "Intro to Programming", "open", threeDaysAhead, "online", null);
 
        sessionRepository.saveAll(List.of(s1, s2, s3));
 
        System.out.println("✅ EULO seed data loaded successfully.");
    }
}
