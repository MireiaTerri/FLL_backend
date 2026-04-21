package cat.udl.eps.softarch.fll.service.ranking;

import cat.udl.eps.softarch.fll.domain.match.MatchResult;
import cat.udl.eps.softarch.fll.domain.ranking.Ranking;
import cat.udl.eps.softarch.fll.repository.match.MatchResultRepository;
import cat.udl.eps.softarch.fll.repository.ranking.RankingRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankingService {

	private final MatchResultRepository matchResultRepository;
	private final RankingRepository rankingRepository;
	private final RankingCalculator rankingCalculator;

	public RankingService(
		MatchResultRepository matchResultRepository,
		RankingRepository rankingRepository,
		RankingCalculator rankingCalculator) {
		this.matchResultRepository = matchResultRepository;
		this.rankingRepository = rankingRepository;
		this.rankingCalculator = rankingCalculator;
	}

	@Transactional
	public void recalculateRanking() {
		List<MatchResult> allResults = matchResultRepository.findAll();
		List<Ranking> rankings = rankingCalculator.calculate(allResults);
		rankingRepository.deleteAllInBatch();
		rankingRepository.saveAll(rankings);
	}
}
