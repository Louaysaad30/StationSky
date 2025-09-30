package tn.esprit.spring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ISkierRepository;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SkierIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ISkierRepository skierRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Skier testSkier;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test subscription
        testSubscription = new Subscription();
        testSubscription.setTypeSub(TypeSubscription.ANNUAL);
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setPrice(500.0f);

        // Create test skier
        testSkier = new Skier();
        testSkier.setFirstName("John");
        testSkier.setLastName("Doe");
        testSkier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        testSkier.setCity("Tunis");
        testSkier.setSubscription(testSubscription);
        testSkier.setRegistrations(new HashSet<>());
    }

    @Test
    void addSkier_IntegrationTest_ShouldCreateAndReturnSkier() throws Exception {
        mockMvc.perform(post("/skier/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSkier)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.city").value("Tunis"));
    }

    @Test
    void getAllSkiers_IntegrationTest_ShouldReturnAllPersistedSkiers() throws Exception {
        // Given - Create and save skiers directly to database
        Skier savedSkier = skierRepository.save(testSkier);

        // When & Then
        mockMvc.perform(get("/skier/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }
}