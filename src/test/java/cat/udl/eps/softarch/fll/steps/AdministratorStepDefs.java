package cat.udl.eps.softarch.fll.steps;

import cat.udl.eps.softarch.fll.domain.Administrator;
import cat.udl.eps.softarch.fll.repository.AdministratorRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdministratorStepDefs {

	private final StepDefs stepDefs;
	private final AdministratorRepository administratorRepository;

	public AdministratorStepDefs(StepDefs stepDefs, AdministratorRepository administratorRepository) {
		this.stepDefs = stepDefs;
		this.administratorRepository = administratorRepository;
	}

	@Given("There is an administrator with username {string} and email {string} and password {string}")
	public void thereIsAnAdministrator(String username, String email, String password) {
		if (!administratorRepository.existsById(username)) {
			Administrator admin = new Administrator();
			admin.setId(username);
			admin.setEmail(email);
			admin.setPassword(password);
			admin.encodePassword();
			administratorRepository.save(admin);
		}
	}

	@When("I create a new administrator with username {string}, email {string} and password {string}")
	public void iCreateAdministrator(String username, String email, String password) throws Exception {
		Administrator admin = new Administrator();
		admin.setId(username);
		admin.setEmail(email);

		stepDefs.result = stepDefs.mockMvc.perform(
				post("/administrators")
					.contentType(MediaType.APPLICATION_JSON)
					.content(new JSONObject(
						stepDefs.mapper.writeValueAsString(admin))
						.put("password", password).toString())
					.characterEncoding(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@When("I retrieve the administrator with username {string}")
	public void iRetrieveAdministrator(String username) throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				get("/administrators/{username}", username)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@When("I update administrator {string} email to {string}")
	public void iUpdateAdministratorEmail(String username, String newEmail) throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				patch("/administrators/{username}", username)
					.contentType(MediaType.APPLICATION_JSON)
					.content(stepDefs.mapper.writeValueAsString(Map.of("email", newEmail)))
					.characterEncoding(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@When("I delete the administrator with username {string}")
	public void iDeleteAdministrator(String username) throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				delete("/administrators/{username}", username)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@And("It has been created an administrator with username {string} and email {string}")
	public void itHasBeenCreatedAnAdministrator(String username, String email) throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				get("/administrators/{username}", username)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print())
			.andExpect(jsonPath("$.email", is(email)))
			.andExpect(jsonPath("$.password").doesNotExist());
	}

	@And("The response contains administrator email {string}")
	public void theResponseContainsAdminEmail(String email) throws Exception {
		stepDefs.result
			.andExpect(jsonPath("$.email", is(email)));
	}

	@And("The administrator with username {string} does not exist")
	public void theAdministratorDoesNotExist(String username) throws Exception {
		stepDefs.mockMvc.perform(
				get("/administrators/{username}", username)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andExpect(status().isNotFound());
	}
}

