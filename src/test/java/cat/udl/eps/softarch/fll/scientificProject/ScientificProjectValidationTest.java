package cat.udl.eps.softarch.fll.scientificProject;

import cat.udl.eps.softarch.fll.domain.DomainValidationException;
import cat.udl.eps.softarch.fll.domain.project.ScientificProject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScientificProjectValidationTest {

	@Test
	void validConstructionWithScore() {
		assertDoesNotThrow(() -> ScientificProject.create(85, "My Project"));
	}

	@Test
	void validConstructionWithZeroScore() {
		assertDoesNotThrow(() -> ScientificProject.create(0, "My Project"));
	}

	@Test
	void invalidConstructionWithNullScore() {
		assertThrows(DomainValidationException.class, () -> ScientificProject.create(null, "My Project"));
	}

	@Nested
	class NegativeScore {

		@Test
		void negativeScoreThrows() {
			assertThrows(DomainValidationException.class,
				() -> ScientificProject.create(-1, "My Project"));
		}

		@Test
		void largeNegativeScoreThrows() {
			assertThrows(DomainValidationException.class,
				() -> ScientificProject.create(-100, "My Project"));
		}
	}
}
