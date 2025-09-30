package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.services.ISkierServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkierRestController.class)
@ActiveProfiles("test")
class SkierRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISkierServices skierServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Skier testSkier;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        // Create test subscription
        testSubscription = new Subscription();
        testSubscription.setNumSub(1L);
        testSubscription.setTypeSub(TypeSubscription.ANNUAL);
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setEndDate(LocalDate.now().plusYears(1));
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
    }

    @Test
    void addSkier_ShouldReturnCreatedSkier() throws Exception {
        // Given
        when(skierServices.addSkier(any(Skier.class))).thenReturn(testSkier);

        // When & Then
        mockMvc.perform(post("/skier/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSkier)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.city").value("Tunis"));
    }

    @Test
    void addSkierAndAssignToCourse_ShouldReturnSkierAssignedToCourse() throws Exception {
        // Given
        Long courseId = 1L;
        when(skierServices.addSkierAndAssignToCourse(any(Skier.class), eq(courseId)))
                .thenReturn(testSkier);

        // When & Then
        mockMvc.perform(post("/skier/addAndAssign/{numCourse}", courseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSkier)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void assignToSubscription_ShouldReturnAssignedSkier() throws Exception {
        // Given
        Long skierId = 1L;
        Long subscriptionId = 1L;
        when(skierServices.assignSkierToSubscription(skierId, subscriptionId))
                .thenReturn(testSkier);

        // When & Then
        mockMvc.perform(put("/skier/assignToSub/{numSkier}/{numSub}", skierId, subscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(1L))
                .andExpect(jsonPath("$.subscription.numSub").value(1L));
    }

    @Test
    void assignToPiste_ShouldReturnAssignedSkier() throws Exception {
        // Given
        Long skierId = 1L;
        Long pisteId = 1L;
        when(skierServices.assignSkierToPiste(skierId, pisteId)).thenReturn(testSkier);

        // When & Then
        mockMvc.perform(put("/skier/assignToPiste/{numSkier}/{numPiste}", skierId, pisteId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(1L));
    }

    @Test
    void retrieveSkiersBySubscriptionType_ShouldReturnFilteredSkiers() throws Exception {
        // Given
        TypeSubscription subscriptionType = TypeSubscription.ANNUAL;
        List<Skier> skiers = Arrays.asList(testSkier);
        when(skierServices.retrieveSkiersBySubscriptionType(subscriptionType)).thenReturn(skiers);

        // When & Then
        mockMvc.perform(get("/skier/getSkiersBySubscription")
                .param("typeSubscription", subscriptionType.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].numSkier").value(1L));
    }

    @Test
    void getById_WithExistingId_ShouldReturnSkier() throws Exception {
        // Given
        Long skierId = 1L;
        when(skierServices.retrieveSkier(skierId)).thenReturn(testSkier);

        // When & Then
        mockMvc.perform(get("/skier/get/{id-skier}", skierId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getById_WithNonExistentId_ShouldReturnNull() throws Exception {
        // Given
        Long skierId = 999L;
        when(skierServices.retrieveSkier(skierId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/skier/get/{id-skier}", skierId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        // Given
        Long skierId = 1L;

        // When & Then
        mockMvc.perform(delete("/skier/delete/{id-skier}", skierId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getAllSkiers_ShouldReturnAllSkiers() throws Exception {
        // Given
        List<Skier> skiers = Arrays.asList(testSkier);
        when(skierServices.retrieveAllSkiers()).thenReturn(skiers);

        // When & Then
        mockMvc.perform(get("/skier/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].numSkier").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }
}