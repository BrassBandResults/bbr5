package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class SiteUserProDao {
    private final SiteUserDao siteUser;
    @Setter private boolean subscriptionActive;
    @Setter private LocalDate currentSubscriptionEndDate;
    @Setter private LocalDate endedAt;
}
