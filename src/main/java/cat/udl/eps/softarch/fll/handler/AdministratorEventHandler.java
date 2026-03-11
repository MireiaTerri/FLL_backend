package cat.udl.eps.softarch.fll.handler;

import cat.udl.eps.softarch.fll.domain.Administrator;
import cat.udl.eps.softarch.fll.repository.AdministratorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class AdministratorEventHandler {

	final Logger logger = LoggerFactory.getLogger(AdministratorEventHandler.class);

	final AdministratorRepository administratorRepository;

	public AdministratorEventHandler(AdministratorRepository administratorRepository) {
		this.administratorRepository = administratorRepository;
	}

	@HandleBeforeCreate
	public void handleAdministratorPreCreate(Administrator administrator) {
		logger.info("Before creating: {}", administrator);
	}

	@HandleBeforeSave
	public void handleAdministratorPreSave(Administrator administrator) {
		logger.info("Before updating: {}", administrator);
	}

	@HandleBeforeDelete
	public void handleAdministratorPreDelete(Administrator administrator) {
		logger.info("Before deleting: {}", administrator);
	}

	@HandleAfterCreate
	public void handleAdministratorPostCreate(Administrator administrator) {
		logger.info("After creating: {}", administrator);
		administrator.encodePassword();
		administratorRepository.save(administrator);
	}

	@HandleAfterSave
	public void handleAdministratorPostSave(Administrator administrator) {
		logger.info("After updating: {}", administrator);
		if (administrator.isPasswordReset()) {
			administrator.encodePassword();
		}
		administratorRepository.save(administrator);
	}
}

