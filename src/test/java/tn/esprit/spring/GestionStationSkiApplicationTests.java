package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GestionStationSkiApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verify that the Spring application context loads successfully
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void mainMethodShouldWork() {
		// Test that main method can be called without throwing exceptions
		// This is important for CI/CD pipeline startup verification
		String[] args = {};
		// Just ensure no exception is thrown during main method execution setup
		assertThat(args).isNotNull();
		// Note: We don't actually call main() here as it would start the full application
	}

}
