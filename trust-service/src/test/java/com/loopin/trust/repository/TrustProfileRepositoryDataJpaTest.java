package com.loopin.trust.repository;

import com.loopin.trust.model.TrustProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TrustProfileRepositoryDataJpaTest {

    @Autowired
    private TrustProfileRepository trustProfileRepository;

    @Test
    @DisplayName("JPA Test: Save and retrieve TrustProfile by student email")
    void shouldPersistAndRetrieveTrustProfile() {
        TrustProfile profile = new TrustProfile(
                null, "clara.b@campus.edu", 82, true, 4, 0, "SILVER"
        );
        trustProfileRepository.save(profile);

        Optional<TrustProfile> found = trustProfileRepository.findByStudentEmail("clara.b@campus.edu");

        assertThat(found).isPresent();
        assertThat(found.get().getTrustScore()).isEqualTo(82);
        assertThat(found.get().getBadgeLevel()).isEqualTo("SILVER");
    }

    @Test
    @DisplayName("JPA Test: Custom JPQL getAverageCampusTrustScore")
    void shouldCalculateAverageCampusScore() {
        trustProfileRepository.save(new TrustProfile(null, "s1@campus.edu", 80, true, 1, 0, "SILVER"));
        trustProfileRepository.save(new TrustProfile(null, "s2@campus.edu", 90, true, 2, 0, "GOLD"));

        Double avg = trustProfileRepository.getAverageCampusTrustScore();

        assertThat(avg).isNotNull();
        assertThat(avg).isGreaterThan(50.0);
    }
}
