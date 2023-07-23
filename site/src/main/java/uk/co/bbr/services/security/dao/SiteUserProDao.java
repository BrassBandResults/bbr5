package uk.co.bbr.services.security.dao;

import com.stripe.model.Subscription;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SiteUserProDao {
    private final SiteUserDao siteUser;
    private final Subscription subscription;
}
