package tn.esprit.spring.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.entities.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ISkierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ISkierRepository skierRepository;

    private Skier testSkier;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        // Create and persist test subscription
        testSubscription = new Subscription();
        testSubscription.setTypeSub(TypeSubscription.ANNUAL);
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setEndDate(LocalDate.now().plusYears(1));
        testSubscription.setPrice(500.0f);
        entityManager.persistAndFlush(testSubscription);

        // Create test skier
        testSkier = new Skier();
        testSkier.setFirstName("John");
        testSkier.setLastName("Doe");
        testSkier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        testSkier.setCity("Tunis");
        testSkier.setSubscription(testSubscription);
    }

    @Test
    void save_ShouldPersistSkier() {
        // When
        Skier savedSkier = skierRepository.save(testSkier);
        entityManager.flush();

        // Then
        assertThat(savedSkier.getNumSkier()).isNotNull();
        assertThat(savedSkier.getFirstName()).isEqualTo("John");
        assertThat(savedSkier.getLastName()).isEqualTo("Doe");
        assertThat(savedSkier.getCity()).isEqualTo("Tunis");
        assertThat(savedSkier.getSubscription()).isEqualTo(testSubscription);
    }

    @Test
    void findById_WithExistingId_ShouldReturnSkier() {
        // Given
        Skier savedSkier = entityManager.persistAndFlush(testSkier);

        // When
        Optional<Skier> foundSkier = skierRepository.findById(savedSkier.getNumSkier());

        // Then
        assertThat(foundSkier).isPresent();
        assertThat(foundSkier.get().getFirstName()).isEqualTo("John");
        assertThat(foundSkier.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // When
        Optional<Skier> foundSkier = skierRepository.findById(999L);

        // Then
        assertThat(foundSkier).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllSkiers() {
        // Given
        Skier skier2 = new Skier();
        skier2.setFirstName("Jane");
        skier2.setLastName("Smith");
        skier2.setDateOfBirth(LocalDate.of(1995, 3, 20));
        skier2.setCity("Sfax");
        skier2.setSubscription(testSubscription);

        entityManager.persistAndFlush(testSkier);
        entityManager.persistAndFlush(skier2);

        // When
        List<Skier> allSkiers = skierRepository.findAll();

        // Then
        assertThat(allSkiers).hasSize(2);
        assertThat(allSkiers).extracting(Skier::getFirstName)
                .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void deleteById_ShouldRemoveSkier() {
        // Given
        Skier savedSkier = entityManager.persistAndFlush(testSkier);
        Long skierId = savedSkier.getNumSkier();

        // When
        skierRepository.deleteById(skierId);
        entityManager.flush();

        // Then
        Optional<Skier> deletedSkier = skierRepository.findById(skierId);
        assertThat(deletedSkier).isEmpty();
    }

    @Test
    void findBySubscription_TypeSub_ShouldReturnFilteredSkiers() {
        // Given
        Subscription monthlySubscription = new Subscription();
        monthlySubscription.setTypeSub(TypeSubscription.MONTHLY);
        monthlySubscription.setStartDate(LocalDate.now());
        monthlySubscription.setEndDate(LocalDate.now().plusMonths(1));
        monthlySubscription.setPrice(100.0f);
        entityManager.persistAndFlush(monthlySubscription);

        entityManager.persistAndFlush(testSkier);

        Skier monthlySkier = new Skier();
        monthlySkier.setFirstName("Jane");
        monthlySkier.setLastName("Smith");
        monthlySkier.setDateOfBirth(LocalDate.of(1995, 3, 20));
        monthlySkier.setCity("Sfax");
        monthlySkier.setSubscription(monthlySubscription);
        entityManager.persistAndFlush(monthlySkier);

        // When
        List<Skier> annualSkiers = skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL);
        List<Skier> monthlySkiers = skierRepository.findBySubscription_TypeSub(TypeSubscription.MONTHLY);

        // Then
        assertThat(annualSkiers).hasSize(1);
        assertThat(annualSkiers.get(0).getFirstName()).isEqualTo("John");

        assertThat(monthlySkiers).hasSize(1);
        assertThat(monthlySkiers.get(0).getFirstName()).isEqualTo("Jane");
    }
}