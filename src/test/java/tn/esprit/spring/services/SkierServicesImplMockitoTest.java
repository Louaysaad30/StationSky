package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Advanced Mockito Test Examples for Skier Service
 * Demonstrates various Mockito features and annotations
 */
@ExtendWith(MockitoExtension.class)
class SkierServicesImplMockitoTest {

    // Different ways to create mocks
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

    @Spy
    private List<Skier> skierListSpy = new ArrayList<>();

    @Captor
    private ArgumentCaptor<Skier> skierCaptor;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    private Skier testSkier;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        // Create test data
        testSubscription = new Subscription();
        testSubscription.setNumSub(1L);
        testSubscription.setTypeSub(TypeSubscription.ANNUAL);
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setPrice(500.0f);

        testSkier = new Skier();
        testSkier.setNumSkier(1L);
        testSkier.setFirstName("John");
        testSkier.setLastName("Doe");
        testSkier.setCity("Tunis");
        testSkier.setSubscription(testSubscription);
        testSkier.setRegistrations(new HashSet<>());
    }

    @Test
    void testBasicMocking() {
        // Given - Basic when/then mocking
        when(skierRepository.findAll()).thenReturn(Arrays.asList(testSkier));

        // When
        List<Skier> result = skierService.retrieveAllSkiers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());

        // Verify method was called
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void testArgumentMatchers() {
        // Given - Using argument matchers
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(testSkier));
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        Skier foundSkier = skierService.retrieveSkier(1L);
        Skier savedSkier = skierService.addSkier(testSkier);

        // Then
        assertNotNull(foundSkier);
        assertNotNull(savedSkier);

        // Verify with argument matchers
        verify(skierRepository).findById(eq(1L));
        verify(skierRepository).save(argThat(skier -> 
            skier.getFirstName().equals("John") && skier.getLastName().equals("Doe")
        ));
    }

    @Test
    void testArgumentCaptor() {
        // Given
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        skierService.addSkier(testSkier);

        // Then - Capture and verify arguments
        verify(skierRepository).save(skierCaptor.capture());
        
        Skier capturedSkier = skierCaptor.getValue();
        assertEquals("John", capturedSkier.getFirstName());
        assertEquals("Tunis", capturedSkier.getCity());
        assertNotNull(capturedSkier.getSubscription().getEndDate());
    }

    @Test
    void testMultipleArgumentCaptures() {
        // Given
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(testSkier));
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.of(testSubscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        skierService.assignSkierToSubscription(1L, 2L);

        // Then - Capture multiple arguments
        verify(skierRepository).findById(longCaptor.capture());
        verify(subscriptionRepository).findById(longCaptor.capture());

        List<Long> capturedIds = longCaptor.getAllValues();
        assertEquals(2, capturedIds.size());
        assertEquals(1L, capturedIds.get(0));
        assertEquals(2L, capturedIds.get(1));
    }

    @Test
    void testSpyBehavior() {
        // Given - Using @Spy annotation
        skierListSpy.add(testSkier);

        // When - Call real method
        int size = skierListSpy.size();

        // Then
        assertEquals(1, size);

        // Verify spy was called
        verify(skierListSpy).add(testSkier);
        verify(skierListSpy).size();
    }

    @Test
    void testExceptionHandling() {
        // Given - Mock throwing exception
        when(skierRepository.findById(999L))
            .thenThrow(new RuntimeException("Skier not found"));

        // When/Then - Test exception
        assertThrows(RuntimeException.class, () -> {
            skierRepository.findById(999L);
        });

        verify(skierRepository).findById(999L);
    }

    @Test
    void testDoReturn_DoThrow() {
        // Given - Using doReturn (useful for void methods)
        doReturn(Optional.of(testSkier)).when(skierRepository).findById(1L);
        doThrow(new RuntimeException("Delete failed")).when(skierRepository).deleteById(999L);

        // When/Then
        Optional<Skier> result = skierRepository.findById(1L);
        assertTrue(result.isPresent());

        assertThrows(RuntimeException.class, () -> skierRepository.deleteById(999L));
    }

    @Test
    void testInOrder() {
        // Given
        when(skierRepository.findById(1L)).thenReturn(Optional.of(testSkier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(testSkier);

        // When
        skierService.assignSkierToSubscription(1L, 1L);

        // Then - Verify order of method calls
        InOrder inOrder = inOrder(skierRepository, subscriptionRepository);
        inOrder.verify(skierRepository).findById(1L);
        inOrder.verify(subscriptionRepository).findById(1L);
        inOrder.verify(skierRepository).save(any(Skier.class));
    }

    @Test
    void testVerifyNoMoreInteractions() {
        // Given
        when(skierRepository.findAll()).thenReturn(Arrays.asList(testSkier));

        // When
        skierService.retrieveAllSkiers();

        // Then
        verify(skierRepository).findAll();
        verifyNoMoreInteractions(skierRepository);
        verifyNoInteractions(pisteRepository, courseRepository);
    }

    @Test
    void testCustomAnswers() {
        // Given - Custom answer for complex logic
        when(skierRepository.save(any(Skier.class))).thenAnswer(invocation -> {
            Skier skier = invocation.getArgument(0);
            skier.setNumSkier(100L); // Simulate auto-generated ID
            return skier;
        });

        // When
        Skier result = skierService.addSkier(testSkier);

        // Then
        assertEquals(100L, result.getNumSkier());
    }

    @Test
    void testPartialMocking() {
        // Given - Partial mocking with spy
        SkierServicesImpl spyService = spy(skierService);
        
        when(skierRepository.findAll()).thenReturn(Arrays.asList(testSkier));
        doReturn(testSkier).when(spyService).retrieveSkier(anyLong());

        // When
        List<Skier> allSkiers = spyService.retrieveAllSkiers(); // Real method
        Skier specificSkier = spyService.retrieveSkier(1L);     // Mocked method

        // Then
        assertEquals(1, allSkiers.size());
        assertEquals(testSkier, specificSkier);
    }

    @Test
    void testTimeouts() {
        // Given
        when(skierRepository.findAll()).thenReturn(Arrays.asList(testSkier));

        // When
        List<Skier> result = skierService.retrieveAllSkiers();

        // Then - Verify with timeout (useful for async operations)
        verify(skierRepository, timeout(1000)).findAll();
        assertNotNull(result);
    }

    @Test
    void testMultipleStubbing() {
        // Given - Multiple calls with different returns
        when(skierRepository.findById(1L))
            .thenReturn(Optional.of(testSkier))      // First call
            .thenReturn(Optional.empty())            // Second call
            .thenThrow(new RuntimeException());      // Third call

        // When/Then
        assertTrue(skierRepository.findById(1L).isPresent());     // First call
        assertFalse(skierRepository.findById(1L).isPresent());    // Second call
        assertThrows(RuntimeException.class, () -> 
            skierRepository.findById(1L));                        // Third call
    }

    @Test
    void testBDDStyleMocking() {
        // Given - BDD style (Behavior Driven Development)
        given(skierRepository.findAll()).willReturn(Arrays.asList(testSkier));

        // When
        List<Skier> result = skierService.retrieveAllSkiers();

        // Then
        then(skierRepository).should().findAll();
        assertEquals(1, result.size());
    }
}