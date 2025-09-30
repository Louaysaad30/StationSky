package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkierServicesImplTest {

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private IPisteRepository pisteRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SkierServicesImpl skierService;

    private Skier testSkier;
    private Subscription testSubscription;
    private Course testCourse;
    private Piste testPiste;

    @BeforeEach
    void setUp() {
        // Create test subscription
        testSubscription = new Subscription();
        testSubscription.setNumSub(1L);
        testSubscription.setTypeSub(TypeSubscription.ANNUAL);
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setPrice(500.0f);

        // Create test skier
        testSkier = new Skier();
        testSkier.setNumSkier(1L);
        testSkier.setFirstName("John");
        testSkier.setLastName("Doe");
        testSkier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        testSkier.setCity("Tunis");
        testSkier.setSubscription(testSubscription);
        testSkier.setRegistrations(new HashSet<>());

        // Create test course
        testCourse = new Course();
        testCourse.setNumCourse(1L);
        testCourse.setLevel(1);
        testCourse.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        testCourse.setSupport(Support.SKI);
        testCourse.setPrice(100.0f);
        testCourse.setTimeSlot(6);

        // Create test piste
        testPiste = new Piste();
        testPiste.setNumPiste(1L);
        testPiste.setNamePiste("Blue Slope");
        testPiste.setColor(Color.BLUE);
        testPiste.setLength(1000);
        testPiste.setSlope(15);
    }

    @Test
    void retrieveAllSkiers_ShouldReturnAllSkiers() {
        // Given
        List<Skier> expectedSkiers = Arrays.asList(testSkier);
        when(skierRepository.findAll()).thenReturn(expectedSkiers);

        // When
        List<Skier> actualSkiers = skierService.retrieveAllSkiers();

        // Then
        assertEquals(expectedSkiers, actualSkiers);
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void addSkier_WithAnnualSubscription_ShouldSetCorrectEndDate() {
        // Given
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        Skier result = skierService.addSkier(testSkier);

        // Then
        assertNotNull(result);
        assertEquals(testSkier.getSubscription().getStartDate().plusYears(1), 
                    testSkier.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(testSkier);
    }

    @Test
    void addSkier_WithSemestrielSubscription_ShouldSetCorrectEndDate() {
        // Given
        testSkier.getSubscription().setTypeSub(TypeSubscription.SEMESTRIEL);
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        Skier result = skierService.addSkier(testSkier);

        // Then
        assertNotNull(result);
        assertEquals(testSkier.getSubscription().getStartDate().plusMonths(6), 
                    testSkier.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(testSkier);
    }

    @Test
    void addSkier_WithMonthlySubscription_ShouldSetCorrectEndDate() {
        // Given
        testSkier.getSubscription().setTypeSub(TypeSubscription.MONTHLY);
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        Skier result = skierService.addSkier(testSkier);

        // Then
        assertNotNull(result);
        assertEquals(testSkier.getSubscription().getStartDate().plusMonths(1), 
                    testSkier.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(testSkier);
    }

    @Test
    void assignSkierToSubscription_ShouldAssignSuccessfully() {
        // Given
        Long skierId = 1L;
        Long subscriptionId = 1L;
        
        when(skierRepository.findById(skierId)).thenReturn(Optional.of(testSkier));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(testSubscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        Skier result = skierService.assignSkierToSubscription(skierId, subscriptionId);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription, result.getSubscription());
        verify(skierRepository, times(1)).findById(skierId);
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(skierRepository, times(1)).save(testSkier);
    }

    @Test
    void assignSkierToSubscription_WithNonExistentSkier_ShouldHandleGracefully() {
        // Given
        Long skierId = 999L;
        Long subscriptionId = 1L;
        
        when(skierRepository.findById(skierId)).thenReturn(Optional.empty());
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(testSubscription));

        // When/Then
        assertThrows(NullPointerException.class, () -> {
            skierService.assignSkierToSubscription(skierId, subscriptionId);
        });
    }

    @Test
    void removeSkier_ShouldCallDeleteById() {
        // Given
        Long skierId = 1L;

        // When
        skierService.removeSkier(skierId);

        // Then
        verify(skierRepository, times(1)).deleteById(skierId);
    }

    @Test
    void retrieveSkier_WithExistingId_ShouldReturnSkier() {
        // Given
        Long skierId = 1L;
        when(skierRepository.findById(skierId)).thenReturn(Optional.of(testSkier));

        // When
        Skier result = skierService.retrieveSkier(skierId);

        // Then
        assertNotNull(result);
        assertEquals(testSkier, result);
        verify(skierRepository, times(1)).findById(skierId);
    }

    @Test
    void retrieveSkier_WithNonExistentId_ShouldReturnNull() {
        // Given
        Long skierId = 999L;
        when(skierRepository.findById(skierId)).thenReturn(Optional.empty());

        // When
        Skier result = skierService.retrieveSkier(skierId);

        // Then
        assertNull(result);
        verify(skierRepository, times(1)).findById(skierId);
    }

    @Test
    void retrieveSkiersBySubscriptionType_ShouldReturnFilteredSkiers() {
        // Given
        TypeSubscription subscriptionType = TypeSubscription.ANNUAL;
        List<Skier> expectedSkiers = Arrays.asList(testSkier);
        when(skierRepository.findBySubscription_TypeSub(subscriptionType)).thenReturn(expectedSkiers);

        // When
        List<Skier> result = skierService.retrieveSkiersBySubscriptionType(subscriptionType);

        // Then
        assertEquals(expectedSkiers, result);
        verify(skierRepository, times(1)).findBySubscription_TypeSub(subscriptionType);
    }
}