package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandRelationshipsController {

    private final BandService bandService;
    private final BandRelationshipService bandRelationshipService;

    private static final String REDIRECT_TO_BAND_RELATIONSHIPS = "redirect:/bands/{bandSlug}/edit-relationships";

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-relationships")
    public String bandRelationshipsEdit(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        List<BandRelationshipDao> relationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());
        List<BandRelationshipTypeDao> relationshipTypes = this.bandRelationshipService.listTypes();

        model.addAttribute("Band", band.get());
        model.addAttribute("BandRelationships", relationships);
        model.addAttribute("RelationshipTypes", relationshipTypes);
        return "bands/band-relationships";
    }



    @IsBbrMember
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

    @IsBbrMember
    @PostMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-relationships/add")
    public String bandRelationshipsCreate(@PathVariable("bandSlug") String bandSlug, @RequestParam("RightBandSlug") String rightBandSlug,
                                                                                     @RequestParam(name="RightBandName",required=false) String rightBandName,
                                                                                     @RequestParam("RelationshipTypeId") String relationshipTypeId) {
        Optional<BandDao> leftBand = this.bandService.fetchBySlug(bandSlug);
        if (leftBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        Optional<BandDao> rightBand = this.bandService.fetchBySlug(rightBandSlug);
        if (rightBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        if (rightBandName == null || rightBandName.strip().length() == 0) {
            rightBandName = rightBand.get().getName();
        }

        Optional<BandRelationshipTypeDao> relationshipType = this.bandRelationshipService.fetchTypeById(Long.parseLong(relationshipTypeId));
        if (relationshipType.isEmpty()) {
            throw NotFoundException.bandRelationshipTypeNotFoundById(relationshipTypeId);
        }

        BandRelationshipDao newRelationship = new BandRelationshipDao();
        newRelationship.setLeftBand(leftBand.get());
        newRelationship.setLeftBandName(leftBand.get().getName());
        newRelationship.setRelationship(relationshipType.get());
        newRelationship.setRightBand(rightBand.get());
        newRelationship.setRightBandName(rightBandName);

        this.bandRelationshipService.createRelationship(newRelationship);

        return REDIRECT_TO_BAND_RELATIONSHIPS;
    }
}

