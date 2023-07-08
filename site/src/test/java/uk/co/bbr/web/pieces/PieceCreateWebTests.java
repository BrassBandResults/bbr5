package uk.co.bbr.web.pieces;

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
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:pieces-piece-create-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceCreateWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PieceService pieceService;
    @Autowired private PersonService personService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testCreatePiecePageWorksSuccessfullyForLoggedInUser() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/create/piece", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Create Piece"));
        assertTrue(response.contains("<form action = \"/create/piece\""));
    }

    @Test
    void testSubmitCreatePiecePageSucceedsForLoggedInMember() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        PersonDao composer = this.personService.create("Composer", "A");
        PersonDao arranger = this.personService.create("Arranger", "B");

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "  New   Test    Piece  ");
        map.add("notes", "   These are some notes  ");
        map.add("year", "  2000  ");
        map.add("category", "M");
        map.add("composerName", "A. Composer");
        map.add("composerSlug", "a-composer");
        map.add("arrangerName", "B. Arranger");
        map.add("arrangerSlug", "b-arranger");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/piece", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/pieces"));

        Optional<PieceDao> fetchedPiece = this.pieceService.fetchBySlug("new-test-piece");
        assertTrue(fetchedPiece.isPresent());
        assertEquals("New Test Piece", fetchedPiece.get().getName());
        assertEquals("These are some notes", fetchedPiece.get().getNotes());
        assertEquals("2000", fetchedPiece.get().getYear());
        assertEquals("M", fetchedPiece.get().getCategory().getCode());
        assertEquals("a-composer", fetchedPiece.get().getComposer().getSlug());
        assertEquals("A. Composer", fetchedPiece.get().getComposer().getCombinedName());
        assertEquals("b-arranger", fetchedPiece.get().getArranger().getSlug());
        assertEquals("B. Arranger", fetchedPiece.get().getArranger().getCombinedName());

    }

    @Test
    void testSubmitCreatePiecePageFailsBecauseNameIsRequired() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("notes", "   These are some notes  ");
        map.add("year", "  2000  ");
        map.add("category", "M");
        map.add("composerName", "");
        map.add("composerSlug", "");
        map.add("arrangerName", "");
        map.add("arrangerSlug", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/piece", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A piece must have a name"));
    }
}

