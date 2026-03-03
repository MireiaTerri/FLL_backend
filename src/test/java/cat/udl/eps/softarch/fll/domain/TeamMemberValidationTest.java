package cat.udl.eps.softarch.fll.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamMemberValidationTest {

	private final Team validTeam = Team.create("TestTeam", "Barcelona", 2000, "category");

	@Test
	void validConstruction() {
		assertDoesNotThrow(() -> TeamMember.create("Alice", "Player", validTeam));
	}

	@Nested
	class EmptyName {

		@Test
		void nullNameThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create(null, "Player", validTeam));
		}

		@Test
		void blankNameThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create("  ", "Player", validTeam));
		}

		@Test
		void blankRoleThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create("Alice", "", validTeam));
		}
	}

	@Nested
	class NullRequiredReference {

		@Test
		void nullTeamThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create("Alice", "Player", null));
		}
	}
}
