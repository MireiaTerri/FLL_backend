package cat.udl.eps.softarch.fll.repository;

import java.time.LocalTime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import cat.udl.eps.softarch.fll.domain.Match;
import cat.udl.eps.softarch.fll.domain.Referee;

@Repository
@RepositoryRestResource
public interface MatchRepository extends CrudRepository<Match, Long>, PagingAndSortingRepository<Match, Long> {
	List<Match> findByRefereeAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
			Referee referee, LocalTime endTime, LocalTime startTime, Long id);
}
