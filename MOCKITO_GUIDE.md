# 🧪 Mockito Testing Guide for Spring Boot

## 📋 **Mockito Dependencies Added**

```xml
<!-- Core Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 Integration -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Advanced Features (static methods, final classes) -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <scope>test</scope>
</dependency>
```

## 🎯 **Mockito Annotations**

### **@ExtendWith(MockitoExtension.class)**
```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    // Enables Mockito annotations
}
```

### **@Mock - Create Mock Objects**
```java
@Mock
private ISkierRepository skierRepository;

@Mock
private ISubscriptionRepository subscriptionRepository;
```

### **@InjectMocks - Inject Mocks into Test Subject**
```java
@InjectMocks
private SkierServicesImpl skierService; // Mocks will be injected here
```

### **@Spy - Partial Mocking**
```java
@Spy
private List<Skier> skierList = new ArrayList<>(); // Real object with some methods mocked
```

### **@Captor - Capture Arguments**
```java
@Captor
private ArgumentCaptor<Skier> skierCaptor;
```

---

## 🔧 **Basic Mockito Operations**

### **1. Basic Stubbing**
```java
// When method called, return value
when(repository.findById(1L)).thenReturn(Optional.of(skier));

// When method called with any argument
when(repository.save(any(Skier.class))).thenReturn(skier);
```

### **2. Verification**
```java
// Verify method was called
verify(repository).findById(1L);

// Verify method called specific number of times
verify(repository, times(2)).findAll();
verify(repository, never()).deleteById(anyLong());
verify(repository, atLeast(1)).save(any());
```

### **3. Argument Matchers**
```java
// Common matchers
when(repository.findById(anyLong())).thenReturn(Optional.of(skier));
when(repository.save(any(Skier.class))).thenReturn(skier);
when(repository.findByCity(anyString())).thenReturn(Arrays.asList(skier));

// Custom matchers
verify(repository).save(argThat(s -> s.getFirstName().equals("John")));
```

---

## 🎭 **Advanced Mockito Features**

### **1. Exception Handling**
```java
// Method throws exception
when(repository.findById(999L)).thenThrow(new RuntimeException("Not found"));

// Void method throws exception
doThrow(new RuntimeException()).when(repository).deleteById(999L);
```

### **2. Argument Captors**
```java
@Captor
private ArgumentCaptor<Skier> skierCaptor;

@Test
void testCaptor() {
    service.addSkier(skier);
    
    verify(repository).save(skierCaptor.capture());
    Skier captured = skierCaptor.getValue();
    assertEquals("John", captured.getFirstName());
}
```

### **3. Multiple Return Values**
```java
when(repository.findById(1L))
    .thenReturn(Optional.of(skier))      // First call
    .thenReturn(Optional.empty())        // Second call
    .thenThrow(new RuntimeException());  // Third call
```

### **4. Custom Answers**
```java
when(repository.save(any(Skier.class))).thenAnswer(invocation -> {
    Skier skier = invocation.getArgument(0);
    skier.setNumSkier(100L); // Simulate auto-generated ID
    return skier;
});
```

### **5. Verify Order of Calls**
```java
InOrder inOrder = inOrder(repository1, repository2);
inOrder.verify(repository1).findById(1L);
inOrder.verify(repository2).save(any());
```

---

## 🎪 **Spy vs Mock**

### **Mock (Full Fake)**
```java
@Mock
private List<String> mockList; // Completely fake, no real behavior

when(mockList.size()).thenReturn(10); // Must stub everything
```

### **Spy (Partial Mock)**
```java
@Spy
private List<String> spyList = new ArrayList<>(); // Real object

// Real method calls work
spyList.add("item");
assertEquals(1, spyList.size()); // Real behavior

// Can still stub specific methods
when(spyList.size()).thenReturn(100); // Override specific behavior
```

---

## 🎯 **Testing Patterns with Mockito**

### **1. Service Layer Testing**
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock private Repository repository;
    @InjectMocks private ServiceImpl service;
    
    @Test
    void testBusinessLogic() {
        // Given
        when(repository.findById(1L)).thenReturn(entity);
        
        // When
        Result result = service.processEntity(1L);
        
        // Then
        assertNotNull(result);
        verify(repository).findById(1L);
    }
}
```

### **2. Repository Testing (with @DataJpaTest)**
```java
@DataJpaTest
class RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private SkierRepository repository;
    
    @Test
    void testCustomQuery() {
        // Use real database (H2)
        Skier skier = new Skier();
        entityManager.persistAndFlush(skier);
        
        List<Skier> result = repository.findByCity("Tunis");
        assertEquals(1, result.size());
    }
}
```

### **3. Controller Testing (with @WebMvcTest)**
```java
@WebMvcTest(SkierController.class)
class ControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private SkierService service; // Spring Boot mock
    
    @Test
    void testEndpoint() throws Exception {
        when(service.getSkier(1L)).thenReturn(skier);
        
        mockMvc.perform(get("/skier/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("John"));
    }
}
```

---

## 🚀 **Best Practices**

### **✅ Do's**
```java
// Use descriptive test names
@Test
void addSkier_WithValidData_ShouldReturnSavedSkier() { }

// Use ArgumentCaptor for complex verifications
verify(repository).save(skierCaptor.capture());

// Test both happy path and edge cases
@Test void testWithValidInput() { }
@Test void testWithInvalidInput() { }
@Test void testWithNullInput() { }
```

### **❌ Don'ts**
```java
// Don't over-mock
@Mock private String mockString; // Use real objects when possible

// Don't mock value objects
@Mock private LocalDate mockDate; // Use real LocalDate

// Don't verify every interaction
verify(repository).findById(1L);
verify(repository).save(any()); // Only verify what's important
```

---

## 📊 **Mockito + Jenkins Pipeline**

Your Jenkins pipeline will now run:

```bash
# All tests including Mockito tests
mvn clean test

# Coverage including mocked code paths
mvn jacoco:report
```

**Files tested:**
- ✅ **Service Tests** - Business logic with mocked repositories
- ✅ **Controller Tests** - API endpoints with mocked services  
- ✅ **Repository Tests** - Database operations with real H2
- ✅ **Integration Tests** - Full stack with test database

## 🎭 **Mockito Test Examples Created**

I've created `SkierServicesImplMockitoTest.java` with examples of:
- Basic mocking and verification
- Argument matchers and captors
- Exception handling
- Spy behavior
- BDD style testing
- Custom answers
- Order verification

Run your tests to see all Mockito features in action! 🎉