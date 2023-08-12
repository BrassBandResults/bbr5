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
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-piece-edit-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceEditWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PieceService pieceService;
    @Autowired private PersonService personService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.pieceService.create("Year of the Dragon");
        this.pieceService.create("Contest Music");
        this.personService.create("Composer", "Mr");
        this.personService.create("Arranger", "Mr");

        logoutTestUser();
    }

    @Test
    void testEditGroupPageWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/contest-music/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Contest Music"));
        assertTrue(response.contains("<form action=\"/pieces/contest-music/edit\""));
    }

    @Test
    void testEditGroupPageGetFailsWhereSlugIsNotFound() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/not-a-valid-slug/edit", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditGroupPageSucceeds() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<PersonDao> composer = this.personService.fetchBySlug("mr-composer");
        Optional<PersonDao> arranger = this.personService.fetchBySlug("mr-arranger");
        assertTrue(composer.isPresent());
        assertTrue(arranger.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", " Contest Music 1 ");
        map.add("notes", " These are some notes ");
        map.add("year", " 2011  ");
        map.add("category", PieceCategory.MARCH.getCode());
        map.add("composerName", composer.get().getName());
        map.add("composerSlug", composer.get().getSlug());
        map.add("arrangerName", arranger.get().getName());
        map.add("arrangerSlug", arranger.get().getSlug());
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/pieces/contest-music/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<PieceDao> piece = this.pieceService.fetchBySlug("contest-music");
        assertTrue(piece.isPresent());
        assertEquals("Contest Music 1", piece.get().getName());
        assertEquals("These are some notes", piece.get().getNotes());
        assertEquals(PieceCategory.MARCH, piece.get().getCategory());
        assertEquals("mr-composer", piece.get().getComposer().getSlug());
        assertEquals("mr-arranger", piece.get().getArranger().getSlug());
    }

    @Test
    void testSubmitEditGroupPageFailsBecauseNameIsRequired() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("notes", " These are some notes ");
        map.add("year", " 2011  ");
        map.add("category", PieceCategory.MARCH.getCode());        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/pieces/year-of-the-dragon/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A piece must have a name"));
    }


    @Test
    void testSubmitEditGroupPageFailsWhereSlugIsNotFound() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Scottish Area");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/pieces/not-a-piece-slug/edit", request, String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

