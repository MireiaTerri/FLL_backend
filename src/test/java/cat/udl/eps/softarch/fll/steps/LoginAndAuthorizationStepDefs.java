package cat.udl.eps.softarch.fll.steps;

import cat.udl.eps.softarch.fll.domain.Edition;
import cat.udl.eps.softarch.fll.domain.Venue;
import cat.udl.eps.softarch.fll.repository.EditionRepository;
import cat.udl.eps.softarch.fll.repository.VenueRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LoginAndAuthorizationStepDefs {

	private final StepDefs stepDefs;
	private final EditionRepository editionRepository;
	private final VenueRepository venueRepository;

	private String persistedEditionUri;
	private String persistedVenueUri;

	public LoginAndAuthorizationStepDefs(StepDefs stepDefs,
										 EditionRepository editionRepository,
										 VenueRepository venueRepository) {
		this.stepDefs = stepDefs;
		this.editionRepository = editionRepository;
		this.venueRepository = venueRepository;
	}

	@When("I retrieve my identity")
	public void iRetrieveMyIdentity() throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				get("/identity")
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@And("The identity username is {string}")
	public void theIdentityUsernameIs(String username) throws Exception {
		stepDefs.result
			.andExpect(jsonPath("$.id", is(username)));
	}

	@Given("There is a persisted edition with year {int}, venue {string} and description {string}")
	public void thereIsAPersistedEdition(int year, String venue, String description) {
		Edition edition = Edition.create(year, venue, description);
		edition = editionRepository.save(edition);
		persistedEditionUri = "/editions/" + edition.getId();
	}

	@When("I retrieve the persisted edition")
	public void iRetrieveThePersistedEdition() throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				get(persistedEditionUri)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@Given("There is a persisted venue with name {string} and city {string}")
	public void thereIsAPersistedVenue(String name, String city) {
		Venue venue = Venue.create(name, city);
		venue = venueRepository.save(venue);
		persistedVenueUri = "/venues/" + venue.getId();
	}

	@When("I retrieve the persisted venue")
	public void iRetrieveThePersistedVenue() throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(
				get(persistedVenueUri)
					.accept(MediaType.APPLICATION_JSON)
					.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}
}

