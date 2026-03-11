package cat.udl.eps.softarch.fll.config;

import cat.udl.eps.softarch.fll.domain.Administrator;
import cat.udl.eps.softarch.fll.repository.AdministratorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class DBInitialization {
	private final AdministratorRepository administratorRepository;
	@Value("${default-password}")
	String defaultPassword;
	@Value("${spring.profiles.active:}")
	private String activeProfiles;

	public DBInitialization(AdministratorRepository administratorRepository) {
		this.administratorRepository = administratorRepository;
	}

	@PostConstruct
	public void initializeDatabase() {
		seedDefaultAdministrator();
		if (isTestProfile()) {
			seedTestAdministrator();
		}
	}

	private void seedDefaultAdministrator() {
		if (!administratorRepository.existsById("admin")) {
			Administrator admin = new Administrator();
			admin.setId("admin");
			admin.setEmail("admin@sample.app");
			admin.setPassword(defaultPassword);
			admin.encodePassword();
			administratorRepository.save(admin);
		}
	}

	private void seedTestAdministrator() {
		if (!administratorRepository.existsById("testAdmin")) {
			Administrator testAdmin = new Administrator();
			testAdmin.setId("testAdmin");
			testAdmin.setEmail("testadmin@sample.app");
			testAdmin.setPassword(defaultPassword);
			testAdmin.encodePassword();
			administratorRepository.save(testAdmin);
		}
	}

	private boolean isTestProfile() {
		return Arrays.asList(activeProfiles.split(",")).contains("test");
	}
}
