package uk.co.bbr.web.embed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:embed-band-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandEmbedWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao blackDyke = this.bandService.create("Black Dyke", yorkshire);
        BandDao grimethorpe = this.bandService.create("Grimethorpe", yorkshire);
        BandDao ybs = this.bandService.create("YBS Band", yorkshire);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2199 = this.contestEventService.create(yorkshireArea, LocalDate.of(2199, 3, 2));
        this.contestResultService.addResult(yorkshireArea2199, "3", blackDyke, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2199, "4", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2199, "1", grimethorpe, duncanBeckley);

        ContestEventDao yorkshireArea2011 = this.contestEventService.create(yorkshireArea, LocalDate.of(2011, 3, 2));
        this.contestResultService.addResult(yorkshireArea2011, "3", blackDyke, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2011, "4", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2011, "1", grimethorpe, duncanBeckley);

        ContestEventDao yorkshireArea2012 = this.contestEventService.create(yorkshireArea, LocalDate.of(2012, 3, 3));
        this.contestResultService.addResult(yorkshireArea2012, "7", blackDyke, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2012, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2012, "9", grimethorpe, duncanBeckley);

        ContestDao broadOakWhitFriday = this.contestService.create("Broadoak (Whit Friday)");
        ContestEventDao broadOakWhitFriday2000 = this.contestEventService.create(broadOakWhitFriday, LocalDate.of(2000, 6, 16));
        this.contestResultService.addResult(broadOakWhitFriday2000, "19", blackDyke, davidRoberts);
        this.contestResultService.addResult(broadOakWhitFriday2000, "4", rtb, duncanBeckley);
        this.contestResultService.addResult(broadOakWhitFriday2000, "26", grimethorpe, johnRoberts);

        logoutTestUser();
    }

    @Test
    void testFetchEmbedLegacyJsonPWorksSuccessfully() throws JsonProcessingException {
        String bandSlug = "rothwell-temperance-band";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + this.port + "/embed/band/" + bandSlug + "/results/1", String.class);

        assertEquals("text/javascript", responseEntity.getHeaders().getFirst("Content-Type"));

        String response = responseEntity.getBody();
        assertNotNull(response);

        assertTrue(response.startsWith("bbr_embed_" + bandSlug.replace("-", "_") + "_jsonp(["));
        assertTrue(response.endsWith("]);"));

        StringBuilder json = new StringBuilder();
        for (String eachLine : response.split("\n")) {
            if (eachLine.startsWith("bbr_embed")) {
                json.append("[\n");
                continue;
            }
            if (eachLine.startsWith("]);")) {
                json.append("]\n");
                continue;
            }

            json.append(eachLine);
            json.append("\n");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json.toString());

        Iterator<JsonNode> children = root.elements();

        JsonNode yorkshire2012Result = children.next();
        assertEquals("yorkshire-area", yorkshire2012Result.get("contest_slug").asText());
        assertEquals("2012-03-03", yorkshire2012Result.get("date").asText());
        assertEquals("03 Mar 2012", yorkshire2012Result.get("date_display").asText());
        assertEquals("Yorkshire Area", yorkshire2012Result.get("contest_name").asText());
        assertEquals("1", yorkshire2012Result.get("result").asText());
        assertEquals("david-roberts", yorkshire2012Result.get("conductor_slug").asText());
        assertEquals("David Roberts", yorkshire2012Result.get("conductor_name").asText());
        JsonNode yorkshire2011Result = children.next();
        assertEquals("yorkshire-area", yorkshire2011Result.get("contest_slug").asText());
        assertEquals("2011-03-02", yorkshire2011Result.get("date").asText());
        assertEquals("02 Mar 2011", yorkshire2011Result.get("date_display").asText());
        assertEquals("Yorkshire Area", yorkshire2011Result.get("contest_name").asText());
        assertEquals("4", yorkshire2011Result.get("result").asText());
        assertEquals("john-roberts", yorkshire2011Result.get("conductor_slug").asText());
        assertEquals("John Roberts", yorkshire2011Result.get("conductor_name").asText());
        JsonNode whit2000Result = children.next();
        assertEquals("broadoak-whit-friday", whit2000Result.get("contest_slug").asText());
        assertEquals("2000-06-16", whit2000Result.get("date").asText());
        assertEquals("16 Jun 2000", whit2000Result.get("date_display").asText());
        assertEquals("Broadoak (Whit Friday)", whit2000Result.get("contest_name").asText());
        assertEquals("4", whit2000Result.get("result").asText());
        assertEquals("duncan-beckley", whit2000Result.get("conductor_slug").asText());
        assertEquals("Duncan Beckley", whit2000Result.get("conductor_name").asText());
    }


    @Test
    void testFetchEmbedLegacyWithInvalidSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/embed/band/not-a-real-band/results/1", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testFetchEmbedBandAllResultsJsonPWorksSuccessfully() throws JsonProcessingException {
        String bandSlug = "rothwell-temperance-band";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + this.port + "/embed/band/" + bandSlug + "/results-all/2023", String.class);

        assertEquals("text/javascript", responseEntity.getHeaders().getFirst("Content-Type"));

        String response = responseEntity.getBody();assertNotNull(response);

        assertTrue(response.startsWith("bbr_embed_" + bandSlug.replace("-", "_") + "_all_jsonp(["));
        assertTrue(response.endsWith("]);"));

        StringBuilder json = new StringBuilder();
        for (String eachLine : response.split("\n")) {
            if (eachLine.startsWith("bbr_embed")) {
                json.append("[\n");
                continue;
            }
            if (eachLine.startsWith("]);")) {
                json.append("]\n");
                continue;
            }

            json.append(eachLine);
            json.append("\n");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json.toString());

        Iterator<JsonNode> children = root.elements();

        JsonNode yorkshire2012Result = children.next();
        assertEquals("yorkshire-area", yorkshire2012Result.get("contest_slug").asText());
        assertEquals("2012-03-03", yorkshire2012Result.get("date").asText());
        assertEquals("03 Mar 2012", yorkshire2012Result.get("date_display").asText());
        assertEquals("Yorkshire Area", yorkshire2012Result.get("contest_name").asText());
        assertEquals("1", yorkshire2012Result.get("result").asText());
        assertEquals("david-roberts", yorkshire2012Result.get("conductor_slug").asText());
        assertEquals("David Roberts", yorkshire2012Result.get("conductor_name").asText());
        JsonNode yorkshire2011Result = children.next();
        assertEquals("yorkshire-area", yorkshire2011Result.get("contest_slug").asText());
        assertEquals("2011-03-02", yorkshire2011Result.get("date").asText());
        assertEquals("02 Mar 2011", yorkshire2011Result.get("date_display").asText());
        assertEquals("Yorkshire Area", yorkshire2011Result.get("contest_name").asText());
        assertEquals("4", yorkshire2011Result.get("result").asText());
        assertEquals("john-roberts", yorkshire2011Result.get("conductor_slug").asText());
        assertEquals("John Roberts", yorkshire2011Result.get("conductor_name").asText());
        JsonNode whit2000Result = children.next();
        assertEquals("broadoak-whit-friday", whit2000Result.get("contest_slug").asText());
        assertEquals("2000-06-16", whit2000Result.get("date").asText());
        assertEquals("16 Jun 2000", whit2000Result.get("date_display").asText());
        assertEquals("Broadoak (Whit Friday)", whit2000Result.get("contest_name").asText());
        assertEquals("4", whit2000Result.get("result").asText());
        assertEquals("duncan-beckley", whit2000Result.get("conductor_slug").asText());
        assertEquals("Duncan Beckley", whit2000Result.get("conductor_name").asText());
    }


    @Test
    void testFetchEmbedBandAllResultsJsonPWithInvalidSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/embed/band/not-a-real-band/results-all/2023", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testFetchEmbedBandWhitResultsJsonPWorksSuccessfully() throws JsonProcessingException {
        String bandSlug = "rothwell-temperance-band";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + this.port + "/embed/band/" + bandSlug + "/results-whit/2023", String.class);

        assertEquals("text/javascript", responseEntity.getHeaders().getFirst("Content-Type"));

        String response = responseEntity.getBody();assertNotNull(response);

        assertTrue(response.startsWith("bbr_embed_" + bandSlug.replace("-", "_") + "_whit_jsonp(["));
        assertTrue(response.endsWith("]);"));

        StringBuilder json = new StringBuilder();
        for (String eachLine : response.split("\n")) {
            if (eachLine.startsWith("bbr_embed")) {
                json.append("[\n");
                continue;
            }
            if (eachLine.startsWith("]);")) {
                json.append("]\n");
                continue;
            }

            json.append(eachLine);
            json.append("\n");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json.toString());

        Iterator<JsonNode> children = root.elements();

        JsonNode whit2000Result = children.next();
        assertEquals("broadoak-whit-friday", whit2000Result.get("contest_slug").asText());
        assertEquals("2000-06-16", whit2000Result.get("date").asText());
        assertEquals("16 Jun 2000", whit2000Result.get("date_display").asText());
        assertEquals("Broadoak (Whit Friday)", whit2000Result.get("contest_name").asText());
        assertEquals("4", whit2000Result.get("result").asText());
        assertEquals("duncan-beckley", whit2000Result.get("conductor_slug").asText());
        assertEquals("Duncan Beckley", whit2000Result.get("conductor_name").asText());
    }


    @Test
    void testFetchEmbedBandWhitResultsJsonPWithInvalidSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/embed/band/not-a-real-band/results-whit/2023", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


    @Test
    void testFetchEmbedBandNonWhitResultsJsonPWorksSuccessfully() throws JsonProcessingException {
        String bandSlug = "rothwell-temperance-band";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + this.port + "/embed/band/" + bandSlug + "/results-non_whit/2023", String.class);

        assertEquals("text/javascript", responseEntity.getHeaders().getFirst("Content-Type"));

        String response = responseEntity.getBody();assertNotNull(response);

        assertTrue(response.startsWith("bbr_embed_" + bandSlug.replace("-", "_") + "_non_whit_jsonp(["));
        assertTrue(response.endsWith("]);"));

        StringBuilder json = new StringBuilder();
        for (String eachLine : response.split("\n")) {
            if (eachLine.startsWith("bbr_embed")) {
                json.append("[\n");
                continue;
            }
            if (eachLine.startsWith("]);")) {
                json.append("]\n");
                continue;
            }

            json.append(eachLine);
            json.append("\n");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json.toString());

        Iterator<JsonNode> children = root.elements();

        JsonNode yorkshire2012Result = children.next();
        assertEquals("yorkshire-area", yorkshire2012Result.get("contest_slug").asText());
        assertEquals("2012-03-03", yorkshire2012Result.get("date").asText());
        assertEquals("03 Mar 2012", yorkshire2012Result.get("date_display").asText());
        assertEquals("Yorkshire Area", yorkshire2012Result.get("contest_name").asText());
        assertEquals("1", yorkshire2012Result.get("result").asText());
        assertEquals("david-roberts", yorkshire2012Result.get("conductor_slug").asText());
        assertEquals("David Roberts", yorkshire2012Result.get("conductor_name").asText());
        JsonNode yorkshire2011Result = children.next();
        assertEquals("yorkshire-area", yorkshire2011Result.get("contest_slug").asText());
        assertEquals("2011-03-02", yorkshire2011Result.get("date").asText());
        assertEquals("02 Mar 2011", yorkshire2011Result.get("date_display").asText());
        assertEquals("Yorkshire Area", yorkshire2011Result.get("contest_name").asText());
        assertEquals("4", yorkshire2011Result.get("result").asText());
        assertEquals("john-roberts", yorkshire2011Result.get("conductor_slug").asText());
        assertEquals("John Roberts", yorkshire2011Result.get("conductor_name").asText());

    }


    @Test
    void testFetchEmbedBandNonWhitResultsJsonPWithInvalidSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/embed/band/not-a-real-band/results-non_whit/2023", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

}
