package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.mixins.SlugTools;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService, SlugTools {
}