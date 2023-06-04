package uk.co.bbr.services.payments;

import java.time.LocalDate;

public interface PaymentsService {

    boolean isProAccountActive();

    boolean isProAccountForFree();

    LocalDate getProAccountExpiryDate();

    String fetchStripeBuyButtonId();

    String fetchStripePublishableKey();
}
