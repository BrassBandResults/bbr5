package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-rehearsals-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-rehearsals-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandRehearsalWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private BandService bandService;
    @Autowired private BandRehearsalsService bandRehearsalsService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.bandService.create("Rothwell Temperance Band");

        BandDao bandWithRehearsals = this.bandService.create("Rehearsals Band");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.MONDAY, "1:00pm");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.TUESDAY, "2:00pm");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.WEDNESDAY, "3:00pm");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.THURSDAY, "4:00pm");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.FRIDAY, "5:00pm");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.SATURDAY, "6:00pm");
        this.bandRehearsalsService.createRehearsalDay(bandWithRehearsals, RehearsalDay.SUNDAY, "7:00pm");

        logoutTestUser();
    }

    @Test
    void testEditRehearsalsWithNoDaysWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-rehearsals", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rothwell Temperance Band - Rehearsals - Brass Band Results</title>"));
        assertTrue(response.contains(">Rothwell Temperance Band</a>"));
        assertTrue(response.contains("Rehearsals"));

        assertTrue(response.contains(">Monday<"));
        assertTrue(response.contains(">Friday<"));

        assertFalse(response.contains("1:00pm"));
        assertFalse(response.contains("2:00pm"));
        assertFalse(response.contains("3:00pm"));
        assertFalse(response.contains("4:00pm"));
        assertFalse(response.contains("5:00pm"));
        assertFalse(response.contains("6:00pm"));
        assertFalse(response.contains("7:00pm"));
    }

    @Test
    void testEditRehearsalsWithDaysSetWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rehearsals-band/edit-rehearsals", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rehearsals Band - Rehearsals - Brass Band Results</title>"));
        assertTrue(response.contains(">Rehearsals Band</a>"));
        assertTrue(response.contains("Rehearsals"));

        assertTrue(response.contains(">Monday<"));
        assertTrue(response.contains(">Friday<"));

        assertTrue(response.contains("1:00pm"));
        assertTrue(response.contains("2:00pm"));
        assertTrue(response.contains("3:00pm"));
        assertTrue(response.contains("4:00pm"));
        assertTrue(response.contains("5:00pm"));
        assertTrue(response.contains("6:00pm"));
        assertTrue(response.contains("7:00pm"));
    }

    @Test
    void testEditRehearsalsWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/invalid-band/edit-rehearsals", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSaveRehearsalsWorksSuccessfully() {
        // arrange
        Optional<BandDao> band = this.bandService.fetchBySlug("rothwell-temperance-band");
        assertTrue(band.isPresent());
        List<BandRehearsalDayDao> fetchedRehearsals1 = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        assertEquals(0, fetchedRehearsals1.size());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("sunday-checkbox", "true");
        map.add("monday-checkbox", "true");
        map.add("tuesday-checkbox", "true");
        map.add("wednesday-checkbox", "true");
        map.add("thursday-checkbox", "true");
        map.add("friday-checkbox", "true");
        map.add("saturday-checkbox", "true");
        map.add("sunday-details", "Sun");
        map.add("monday-details", "Mon");
        map.add("tuesday-details", "Tue");
        map.add("wednesday-details", "Wed");
        map.add("thursday-details", "Thu");
        map.add("friday-details", "Fri");
        map.add("saturday-details", "Sat");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/rothwell-temperance-band/edit-rehearsals", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/bands/rothwell-temperance-band"));

        List<BandRehearsalDayDao> fetchedRehearsals2 = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        assertEquals(7, fetchedRehearsals2.size());
        assertEquals("Sun", fetchedRehearsals2.get(0).getDetails());
        assertEquals("Mon", fetchedRehearsals2.get(1).getDetails());
        assertEquals("Tue", fetchedRehearsals2.get(2).getDetails());
        assertEquals("Wed", fetchedRehearsals2.get(3).getDetails());
        assertEquals("Thu", fetchedRehearsals2.get(4).getDetails());
        assertEquals("Fri", fetchedRehearsals2.get(5).getDetails());
        assertEquals("Sat", fetchedRehearsals2.get(6).getDetails());
    }

    @Test
    void testEditRehearsalsPostWithInvalidSlugFails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("sunday-details", "Sun");
        map.add("monday-details", "Mon");
        map.add("tuesday-details", "Tue");
        map.add("wednesday-details", "Wed");
        map.add("thursday-details", "Thu");
        map.add("friday-details", "Fri");
        map.add("saturday-details", "Sat");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/bands/invalid-band/edit-rehearsals", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

