package com.loopin;

import com.loopin.model.ItemListing;
import com.loopin.model.ItemRequest;
import com.loopin.model.User;
import com.loopin.repository.ListingRepository;
import com.loopin.repository.RequestRepository;
import com.loopin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds initial demo data on application startup using JPA repositories.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ListingRepository listingRepository;

    public DatabaseInitializer(UserRepository userRepository,
                               RequestRepository requestRepository,
                               ListingRepository listingRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        logger.info("Pre-populating H2 database with demo Users and ItemRequests...");

        // 1. Create sample Users
        User alex = new User(null, "Alex Morgan", "alex.m@campus.edu", "Computer Science");
        User priya = new User(null, "Priya Sharma", "priya.s@campus.ac.in", "Chemistry");
        User david = new User(null, "David Kim", "david.k@campus.edu", "Media Arts");

        alex = userRepository.save(alex);
        priya = userRepository.save(priya);
        david = userRepository.save(david);

        // 2. Create sample Item Requests linked to parent Users
        LocalDate today = LocalDate.now();

        ItemRequest req1 = new ItemRequest(null, "TI-84 Graphing Calculator", "Academics",
                alex.getName(), alex.getEmail(), today.plusDays(1), today.plusDays(5),
                1, "OPEN", "Needed for Calculus III midterm exam");
        req1.setUser(alex);
        req1.setTrustVerificationStatus("VERIFIED_CAMPUS_MEMBER");
        requestRepository.save(req1);

        ItemRequest req2 = new ItemRequest(null, "Cotton White Lab Coat", "Academics",
                priya.getName(), priya.getEmail(), today.plusDays(2), today.plusDays(4),
                2, "OPEN", "Organic Chemistry Lab on Thursday");
        req2.setUser(priya);
        req2.setTrustVerificationStatus("VERIFIED_CAMPUS_MEMBER");
        requestRepository.save(req2);

        ItemRequest req3 = new ItemRequest(null, "Sony Alpha Camera with Tripod", "Electronics",
                david.getName(), david.getEmail(), today.plusDays(3), today.plusDays(6),
                3, "OPEN", "Weekend film festival project");
        req3.setUser(david);
        req3.setTrustVerificationStatus("VERIFIED_CAMPUS_MEMBER");
        requestRepository.save(req3);

        // 3. Create sample Item Listings
        listingRepository.save(new ItemListing(null, "Texas Instruments TI-84 Plus CE", "Academics",
                "Emma Watson", "emma.w@campus.edu", 1, true, "EXCELLENT"));
        listingRepository.save(new ItemListing(null, "White Lab Coat - 100% Cotton (Unisex M)", "Academics",
                "Rahul Patel", "rahul.p@campus.ac.in", 2, true, "GOOD"));
        listingRepository.save(new ItemListing(null, "Sony A6400 Mirrorless Camera & 50mm Lens", "Electronics",
                "Sophia Chen", "sophia.c@campus.edu", 3, true, "EXCELLENT"));
        listingRepository.save(new ItemListing(null, "Anker USB-C to HDMI 4K Adapter", "Electronics",
                "Liam Davis", "liam.d@campus.edu", 2, true, "GOOD"));

        logger.info("H2 database successfully seeded with {} users, {} requests, and {} listings.",
                userRepository.count(), requestRepository.count(), listingRepository.count());
    }
}
