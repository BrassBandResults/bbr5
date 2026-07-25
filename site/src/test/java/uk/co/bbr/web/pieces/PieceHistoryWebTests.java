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
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dao.PieceHistoryDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
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
        "spring.datasource.url=jdbc:h2:mem:piece-history-web-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceHistoryWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PieceService pieceService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    private PieceDao piece;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_SUPERUSER);
        this.securityService.makeUserSuperuser(TestUser.TEST_SUPERUSER.getUsername());
        this.piece = this.pieceService.create("History Piece");

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        logoutTestUser();
    }

    @Test
    void testHistoryLinkVisibleToSuperuser() {
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/history-piece", String.class);
        assertNotNull(response);
        assertTrue(response.contains("/pieces/history-piece/history"));
        assertTrue(response.contains("History"));
    }

    @Test
    void testHistoryLinkNotVisibleToMember() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/history-piece", String.class);
        assertNotNull(response);
        assertFalse(response.contains("/pieces/history-piece/history"));
    }

    @Test
    void testEditLinkVisibleToMember() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/history-piece", String.class);
        assertNotNull(response);
        assertTrue(response.contains("/pieces/history-piece/edit"));
    }

    @Test
    void testHistoryPageAccessibleToSuperuser() {
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/history-piece/history", String.class);
        assertNotNull(response);
        assertTrue(response.contains("History of History Piece"));
    }

    @Test
    void testHistoryPageNotAccessibleToMember() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/history-piece/history", String.class));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testHistoryIsRecordedOnUpdate() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Updated History Piece");
        map.add("notes", "New Notes");
        map.add("category", PieceCategory.HYMN.getCode());
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        this.restTemplate.postForEntity("http://localhost:" + port + "/pieces/history-piece/edit", request, String.class);

        // assert history
        Optional<PieceDao> updatedPiece = this.pieceService.fetchBySlug("history-piece");
        assertTrue(updatedPiece.isPresent());
        
        List<PieceHistoryDao> history = this.pieceService.fetchHistory(updatedPiece.get());
        // Should have 2 entries: one from create, one from update
        assertEquals(2, history.size());
        assertEquals("Updated History Piece", history.get(0).getName());
        assertEquals("New Notes", history.get(0).getNotes());
        assertEquals("History Piece", history.get(1).getName());
    }
}
