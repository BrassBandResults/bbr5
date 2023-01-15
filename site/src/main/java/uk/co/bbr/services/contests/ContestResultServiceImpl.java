package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.repo.ContestEventRepository;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.repo.ContestResultRepository;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.contests.types.ResultPositionType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestResultServiceImpl implements ContestResultService {

    private final ContestResultRepository contestResultRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public ContestResultDao addResult(ContestEventDao event, ContestResultDao result) {
        result.setContestEvent(event);

        ContestResultDao returnResult = null;
        // is there an existing result for the same band?
        Optional<ContestResultDao> existingResult = this.contestResultRepository.fetchForEventAndBand(event.getId(), result.getBand().getId());
        if (existingResult.isPresent()) {
            ContestResultDao existingResultObject = existingResult.get();
            existingResultObject.populateFrom(result);
            existingResultObject.setUpdated(LocalDateTime.now());
            existingResultObject.setUpdatedBy(this.securityService.getCurrentUser());
            returnResult = this.contestResultRepository.saveAndFlush(existingResultObject);
        } else {
            result.setCreated(LocalDateTime.now());
            result.setCreatedBy(this.securityService.getCurrentUser());
            result.setUpdated(LocalDateTime.now());
            result.setUpdatedBy(this.securityService.getCurrentUser());
            returnResult = this.contestResultRepository.saveAndFlush(result);
        }

        return returnResult;
    }

    @Override
    public List<ContestResultDao> fetchForEvent(ContestEventDao event) {
        return this.contestResultRepository.findAllForEvent(event.getId());
    }
}
