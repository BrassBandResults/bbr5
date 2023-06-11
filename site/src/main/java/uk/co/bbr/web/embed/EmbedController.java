package uk.co.bbr.web.embed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.BandResultService;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EmbedController {

    private final BandService bandService;
    private final BandResultService bandResultService;

    @GetMapping("/embed/band/{bandSlug:[\\-a-z\\d]{2,}}/results-{type:all|non_whit|whit}/2023")
    public String embedBandResultsJsonP(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("type") String type) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.PAST);

        List<ContestResultDao> resultsToReturn = null;
        switch (type) {
            case "non_whit" -> resultsToReturn = bandResults.getBandNonWhitResults();
            case "whit" -> resultsToReturn = bandResults.getBandWhitResults();
            case "all" -> resultsToReturn = bandResults.getBandAllResults();
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("BandSlugUnderscores", band.get().getSlugWithUnderscores());
        model.addAttribute("Results", resultsToReturn);
        model.addAttribute("Type", type.replace("-","_"));

        return "embed/band-2023-jsonp";
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
        return "<!-- Start http://brassbandresults.co.uk/ embedding code v2023 -->\n" +
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
                "    lTable += '<td><a href='https://brassbandresults.co.uk/contests/' + data[i].contest_slug + '/' + data[i].date + '/' title='Click here to view full results for this contest on brassbandresults.co.uk' target='_blank'>' + data[i].date_display + '</a></td>';\n" +
                "    lTable += '<td>' + data[i].contest_name + '</td>';\n" +
                "    lTable += '<td>' + data[i].result + '</td>';\n" +
                "    lTable += '<td><a href='https://brassbandresults.co.uk/people/' + data[i].conductor_slug + '/' title='Click here to view all results for this conductor on www.brassbandresults.co.uk' target='_blank'>' + data[i].conductor_name + '</a></td>';\n" +
                "    lTable += '</tr>';\n" +
                "  }\n" +
                "  document.getElementById('#bbr-" + bandDao.getSlug() + "-results_" + type + "').innerHtml = resultsTable;\n" +
                "}\n" +
                "</script><br/><font size='-1'>Results provided by <a href='https://brassbandresults.co.uk/'>https://brassbandresults.co.uk</a></font>\n" +
                "<script src='https://brassbandresults.co.uk/embed/band/rothwell-temperance-band/results-" + type + "/2023/'></script>\n" +
                "<!-- End https://brassbandresults.co.uk/ embedding code v2023 -->\n";
    }
}

