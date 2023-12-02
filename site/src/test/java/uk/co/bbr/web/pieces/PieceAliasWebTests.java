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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.PieceAliasService;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:piece-alias-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceAliasWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PieceService pieceService;
    @Autowired private PieceAliasService pieceAliasService;
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

        PieceDao piece = this.pieceService.create("The Year Of The Dragon");
        PieceAliasDao personPreviousName1 = new PieceAliasDao();
        personPreviousName1.setName("Year of the Dragon");
        personPreviousName1.setHidden(false);
        this.pieceAliasService.createAlias(piece, personPreviousName1);
        PieceAliasDao personPreviousName2 = new PieceAliasDao();
        personPreviousName2.setName("t'Year of the Dragon");
        personPreviousName2.setHidden(true);
        this.pieceAliasService.createAlias(piece, personPreviousName2);

        this.pieceService.create("Contest Music");

        logoutTestUser();
    }

    @Test
    void testListAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/the-year-of-the-dragon/edit-aliases", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>The Year Of The Dragon - Piece Aliases - Brass Band Results</title>"));
        assertTrue(response.contains(">The Year Of The Dragon</a>"));
        assertTrue(response.contains("Aliases<"));

        assertTrue(response.contains(">Visible<"));
        assertTrue(response.contains(">Hidden<"));
    }

    @Test
    void testListAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/not-a-real-person/edit-aliases", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testHideAliasWorksSuccessfully() {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug("the-year-of-the-dragon");

        long visibleAliasId = 0;
        List<PieceAliasDao> previousNamesBefore = this.pieceAliasService.findAllAliases(piece.get());
        for (PieceAliasDao previousName : previousNamesBefore) {
            if (previousName.getName().equals("Year of the Dragon")) {
                assertFalse(previousName.isHidden());
                visibleAliasId = previousName.getId();
                break;
            }
        }

        assertTrue(visibleAliasId != 0);

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/the-year-of-the-dragon/edit-aliases/" + visibleAliasId + "/hide", String.class);
        assertNotNull(response);

        List<PieceAliasDao> previousNamesAfter = this.pieceAliasService.findAllAliases(piece.get());
        for (PieceAliasDao previousName : previousNamesAfter) {
            if (previousName.getName().equals("Year of the Dragon")) {
                assertTrue(previousName.isHidden());
                break;
            }
        }

        this.pieceAliasService.showAlias(piece.get(), visibleAliasId);
    }

    @Test
    void testHideAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/not-a-real-person/edit-aliases/1/hide", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testHideAliasesWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/the-year-of-the-dragon/edit-aliases/999/hide", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testShowAliasWorksSuccessfully() {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug("the-year-of-the-dragon");

        long hiddenAliasId = 0;
        List<PieceAliasDao> previousNamesBefore = this.pieceAliasService.findAllAliases(piece.get());
        for (PieceAliasDao previousName : previousNamesBefore) {
            if (previousName.getName().equals("t'Year of the Dragon")) {
                assertTrue(previousName.isHidden());
                hiddenAliasId = previousName.getId();
                break;
            }
        }

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/the-year-of-the-dragon/edit-aliases/" + hiddenAliasId + "/show", String.class);
        assertNotNull(response);

        List<PieceAliasDao> previousNamesAfter = this.pieceAliasService.findAllAliases(piece.get());
        for (PieceAliasDao previousName : previousNamesAfter) {
            if (previousName.getName().equals("t'Year of the Dragon")) {
                assertFalse(previousName.isHidden());
                break;
            }
        }

        this.pieceAliasService.hideAlias(piece.get(), hiddenAliasId);
    }

    @Test
    void testShowAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/not-a-real-person/edit-aliases/1/show", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testShowAliasesWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/the-year-of-the-dragon/edit-aliases/999/show", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<PieceDao> noAliasPerson = this.pieceService.fetchBySlug("contest-music");
        assertTrue(noAliasPerson.isPresent());

        List<PieceAliasDao> fetchedAliases1 = this.pieceAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(0, fetchedAliases1.size());

        PieceAliasDao previousName = new PieceAliasDao();
        previousName.setName("Old Name To Delete");
        PieceAliasDao newAlias = this.pieceAliasService.createAlias(noAliasPerson.get(), previousName);

        List<PieceAliasDao> fetchedAliases2 = this.pieceAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(1, fetchedAliases2.size());

        logoutTestUser();

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/contest-music/edit-aliases/" + newAlias.getId() + "/delete", String.class);
        assertNotNull(response);

        List<PieceAliasDao> fetchedAliases3 = this.pieceAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(0, fetchedAliases3.size());
    }

    @Test
    void testDeleteAliasWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/not-a-real-person/edit-aliases/1/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/the-year-of-the-dragon/edit-aliases/999/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateAliasWorksSuccessfully() {
        // arrange
        Optional<PieceDao> band = this.pieceService.fetchBySlug("the-year-of-the-dragon");
        assertTrue(band.isPresent());
        List<PieceAliasDao> fetchedAliases1 = this.pieceAliasService.findAllAliases(band.get());
        assertEquals(2, fetchedAliases1.size());
        long aliasId = fetchedAliases1.get(0).getId();
        assertEquals("Year of the Dragon", fetchedAliases1.get(0).getName());
        assertEquals("t'Year of the Dragon", fetchedAliases1.get(1).getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "New Alias");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/pieces/the-year-of-the-dragon/edit-aliases/add", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<PieceAliasDao> fetchedAliases2 = this.pieceAliasService.findAllAliases(band.get());
        assertEquals(3, fetchedAliases2.size());
        assertEquals("Year of the Dragon", fetchedAliases2.get(0).getName());
        assertEquals("t'Year of the Dragon", fetchedAliases2.get(1).getName());
        assertEquals("New Alias", fetchedAliases2.get(2).getName());
        assertFalse(fetchedAliases2.get(2).isHidden());
    }

    @Test
    void testCreateAliasWithInvalidBandSlugFailsAsExpected() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "New Alias");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/pieces/not-a-real-person/edit-aliases/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

