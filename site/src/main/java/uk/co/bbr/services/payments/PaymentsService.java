package uk.co.bbr.services.payments;

import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;

import java.time.LocalDate;
import java.util.List;

public interface PaymentsService {

    boolean isProAccountActive();

    boolean isProAccountForFree();

    LocalDate getProAccountExpiryDate();

    String fetchStripeBuyButtonId();

    String fetchStripePublishableKey();

    List<SiteUserProDao> markupUsers(List<SiteUserDao> users);
}
