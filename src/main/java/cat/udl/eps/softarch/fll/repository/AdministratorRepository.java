package cat.udl.eps.softarch.fll.repository;

import cat.udl.eps.softarch.fll.domain.Administrator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "Administrators", description = "Repository for managing Administrator entities")
@RepositoryRestResource
public interface AdministratorRepository extends CrudRepository<Administrator, String>,
	PagingAndSortingRepository<Administrator, String> {
}

