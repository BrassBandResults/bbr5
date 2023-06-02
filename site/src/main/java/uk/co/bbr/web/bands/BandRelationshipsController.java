package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandRelationshipsController {

    private final BandService bandService;
    private final BandRelationshipService bandRelationshipService;

    private static final String REDIRECT_TO_BAND_RELATIONSHIPS = "redirect:/bands/{bandSlug}/edit-relationships";

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-relationships")
    public String bandRelationshipsEdit(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        List<BandRelationshipDao> relationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("BandRelationships", relationships);
        return "bands/band-relationships";
    }



    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-relationships/{relationshipId:\\d+}/delete")
    public String bandRelationshipsDelete(@PathVariable("bandSlug") String bandSlug, @PathVariable("relationshipId") Long relationshipId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        Optional<BandRelationshipDao> relationship = this.bandRelationshipService.fetchById(relationshipId);
        if (relationship.isEmpty()) {
            throw NotFoundException.relationshipNotFoundById(relationshipId);
        }

        this.bandRelationshipService.deleteRelationship(relationship.get());

        return REDIRECT_TO_BAND_RELATIONSHIPS;
    }

//    @PostMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-relationships/add")
//    public String bandRelationshipsCreate(@PathVariable("bandSlug") String bandSlug, @RequestParam("oldName") String oldName) {
//        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
//        if (band.isEmpty()) {
//            throw NotFoundException.bandNotFoundBySlug(bandSlug);
//        }
//
//        this.bandRelationshipService.createRelationship(leftBand.get(), rightBand.get(), type);
//
//        return REDIRECT_TO_BAND_RELATIONSHIPS;
//    }
}

