package uk.co.bbr.web.embed;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.events.BandResultService;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EmbedController {

    private final BandService bandService;
    private final BandResultService bandResultService;
    private final ApplicationContext applicationContext;

    private String resolveTemplate(String template, BandDao band, List<ContestResultDao> bandResults, String type) {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".jsonp");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("utf-8");
        templateResolver.setCacheable(false);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        for (ContestResultDao eachResult : bandResults) {
            if (eachResult.getConductor() == null) {
                eachResult.setConductor(new PersonDao());
                eachResult.getConductor().setSurname("Unknown");
                eachResult.getConductor().setSlug("unknown");
            }
        }

        Context context = new Context();
        context.setVariable("Band", band);
        context.setVariable("BandSlugUnderscores", band.getSlugWithUnderscores());
        context.setVariable("Results", bandResults);
        context.setVariable("Type", type.replace("-","_"));

        return templateEngine.process(template, context);
    }

    @GetMapping("/embed/band/{bandSlug:[\\-a-z\\d]{2,}}/results/{version:\\d}")
    public ResponseEntity<String> embedBandResults(@PathVariable("bandSlug") String bandSlug, @PathVariable("version") int version) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.PAST);
        String json = this.resolveTemplate("embed/band-legacy", band.get(), bandResults.getBandAllResults(), "");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/javascript");
        responseHeaders.setAccessControlAllowOrigin("*");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/embed/band/{bandSlug:[\\-a-z\\d]{2,}}/results-{type:all|non_whit|whit}/2023")
    public ResponseEntity<String> embedBandResultsJsonP(@PathVariable("bandSlug") String bandSlug, @PathVariable("type") String type) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.PAST);


        List<ContestResultDao> resultsToReturn;
        switch (type) {
            case "non_whit" -> resultsToReturn = bandResults.getBandNonWhitResults();
            case "whit" -> resultsToReturn = bandResults.getBandWhitResults();
            default -> resultsToReturn = bandResults.getBandAllResults();
        }

        String json = this.resolveTemplate("embed/band-2023", band.get(), resultsToReturn, type);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/javascript");
        responseHeaders.setAccessControlAllowOrigin("*");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/embed")
    public String embedBandResultsAll(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        String embedCode = this.createEmbedCode(band.get(), "all");

        model.addAttribute("Band", band.get());
        model.addAttribute("EmbedCode", embedCode);
        model.addAttribute("Type", "embed.type.all");
        return "embed/band-results-embed";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/embed/non_whit")
    public String embedBandResultsNonWhitFriday(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        String embedCode = this.createEmbedCode(band.get(), "non_whit");

        model.addAttribute("Band", band.get());
        model.addAttribute("EmbedCode", embedCode);
        model.addAttribute("Type", "embed.type.non-whit");
        return "embed/band-results-embed";
    }


    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/embed/whit")
    public String embedBandResultsWhitFriday(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        String embedCode = this.createEmbedCode(band.get(), "whit");

        model.addAttribute("Band", band.get());
        model.addAttribute("EmbedCode", embedCode);
        model.addAttribute("Type", "embed.type.whit");
        return "embed/band-results-embed";
    }


    private String createEmbedCode(BandDao bandDao, String type) {
        return "<!-- Start https://www.brassbandresults.co.uk/ embedding code v2023 -->\n" +
                "<table><thead><tr>\n" +
                "  <th>Date</th>\n" +
                "  <th>Contest</th>\n" +
                "  <th>Position</th>\n" +
                "  <th>Conductor</th>\n" +
                "</tr></thead><tbody id='bbr-" + bandDao.getSlug() + "-results_"+ type +"'></tbody></table>\n" +
                "<script>\n" +
                "function bbr_embed_" + bandDao.getSlugWithUnderscores() + "_" + type + "_jsonp(data) {\n" +
                "  let lTable = '';\n" +
                "  for (var i=0; i<data.length; i++) {\n" +
                "    lTable += '<tr>';\n" +
                "    lTable += '<td><a href='https://www.brassbandresults.co.uk/contests/' + data[i].contest_slug + '/' + data[i].date + '/' title='Click here to view full results for this contest on brassbandresults.co.uk' target='_blank'>' + data[i].date_display + '</a></td>';\n" +
                "    lTable += '<td>' + data[i].contest_name + '</td>';\n" +
                "    lTable += '<td>' + data[i].result + '</td>';\n" +
                "    lTable += '<td><a href='https://www.brassbandresults.co.uk/people/' + data[i].conductor_slug + '/' title='Click here to view all results for this conductor on www.brassbandresults.co.uk' target='_blank'>' + data[i].conductor_name + '</a></td>';\n" +
                "    lTable += '</tr>';\n" +
                "  }\n" +
                "  document.getElementById('#bbr-" + bandDao.getSlug() + "-results_" + type + "').innerHtml = resultsTable;\n" +
                "}\n" +
                "</script><br/><font size='-1'>Results provided by <a href='https://www.brassbandresults.co.uk/'>https://www.brassbandresults.co.uk</a></font>\n" +
                "<script src='https://www.brassbandresults.co.uk/embed/band/rothwell-temperance-band/results-" + type + "/2023/'></script>\n" +
                "<!-- End https://www.brassbandresults.co.uk/ embedding code v2023 -->\n";
    }
}

