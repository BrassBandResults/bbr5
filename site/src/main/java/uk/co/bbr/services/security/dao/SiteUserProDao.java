package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class SiteUserProDao {
    private final SiteUserDao siteUser;
    private final boolean subscriptionActive;
    private final LocalDate currentSubscriptionEndDate;
    private final LocalDate endedAt;
}
