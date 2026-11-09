package com.loopin.trust;

import com.loopin.trust.model.TrustProfile;
import com.loopin.trust.repository.TrustProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Pre-populates Microservice 2 H2 database with demo trust profiles.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final TrustProfileRepository trustProfileRepository;

    public DatabaseInitializer(TrustProfileRepository trustProfileRepository) {
        this.trustProfileRepository = trustProfileRepository;
    }

    @Override
    public void run(String... args) {
        if (trustProfileRepository.count() > 0) {
            return;
        }

        logger.info("Initializing Microservice 2 H2 database with demo trust profiles...");

        trustProfileRepository.save(new TrustProfile(
                null, "alex.m@campus.edu", 85, true, 5, 0, "GOLD"
        ));

        trustProfileRepository.save(new TrustProfile(
                null, "priya.s@campus.ac.in", 90, true, 8, 0, "GOLD"
        ));

        trustProfileRepository.save(new TrustProfile(
                null, "david.k@campus.edu", 75, true, 2, 0, "SILVER"
        ));

        trustProfileRepository.save(new TrustProfile(
                null, "probation.user@campus.edu", 45, true, 1, 2, "BRONZE"
        ));

        logger.info("Microservice 2 H2 database seeded with {} trust profiles.", trustProfileRepository.count());
    }
}
