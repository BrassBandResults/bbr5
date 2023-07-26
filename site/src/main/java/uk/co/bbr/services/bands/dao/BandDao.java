package uk.co.bbr.services.bands.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.map.dto.LocationPoint;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name="band")
public class BandDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="website")
    private String website;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="region_id")
    @Setter
    private RegionDao region;

    @Column(name="longitude")
    private String longitude;

    @Column(name="latitude")
    private String latitude;

    @Column(name="notes")
    private String notes;

    @Column(name="mapped_by")
    @Setter
    private String mapper;

    @Column(name="start_date")
    @Setter
    private LocalDate startDate;

    @Column(name="end_date")
    @Setter
    private LocalDate endDate;

    @Column(name="status")
    @Setter
    private BandStatus status;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="section_id")
    @Setter
    private SectionDao section;

    @Column(name="twitter_name")
    private String twitterName;

    @Transient
    private int resultsCount;

    @Transient
    private int contestCount;

    public void setName(String sourceName) {

        if (sourceName == null) {
            this.name = null;
            return;
        }
        this.name = simplifyBandName(sourceName);
    }

    public void setOldId(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.oldId = value;
    }

    public void setLatitude(String latitude) {
        if (latitude != null) {
            if (latitude.trim().length() > 15) {
                latitude = latitude.trim().substring(0, 15);
            }
            latitude = latitude.trim();
            if (latitude.length() == 0) {
                latitude = null;
            }
        }
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        if (longitude != null) {
            if (longitude.trim().length() > 15) {
                longitude = longitude.trim().substring(0, 15);
            }
            longitude = longitude.trim();
            if (longitude.length() == 0) {
                longitude = null;
            }
        }
        this.longitude = longitude;
    }

    public void setNotes(String notes) {
        if (notes != null) {
            notes = notes.trim();
        }
        this.notes = notes;
    }

    public void setWebsite(String website) {
        if (website != null) {
            website = website.trim();
            if (website.length() == 0 || website.equalsIgnoreCase("http://") || website.equalsIgnoreCase("https://")) {
                website = null;
            }
        }
        this.website = website;
    }

    public void setTwitterName(String twitterName) {
        if (twitterName != null) {
            if (twitterName.trim().startsWith("@")) {
                twitterName = twitterName.trim().substring(1);
            }
        }
        this.twitterName = twitterName;
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.slug = value;
    }

    public String getDateRange() {
        if (this.startDate == null && this.endDate == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        if (this.startDate != null) {
            builder.append(this.startDate.getYear());
        }
        builder.append("-");
        if (this.endDate != null) {
            builder.append(this.endDate.getYear());
        }
        return builder.toString();
    }

    public String getSectionType() {
        if (this.section == null) {
              return this.status.getTranslationKey();
        }
        if (this.section.getTranslationKey().equals("section.youth")) {
            return "status.youth";
        }
        return this.section.getTranslationKey();
    }

    public ObjectNode asGeoJson(ObjectMapper objectMapper) {
        ObjectNode bandGeometry = objectMapper.createObjectNode();
        bandGeometry.put("type", "Point");
        bandGeometry.putArray("coordinates").add(Float.parseFloat(this.getLongitude())).add(Float.parseFloat(this.getLatitude()));

        ObjectNode bandProperties = objectMapper.createObjectNode();
        bandProperties.put("name", this.getName());
        bandProperties.put("slug", this.getSlug());
        bandProperties.put("type", this.getSectionType());

        ObjectNode bandNode = objectMapper.createObjectNode();
        bandNode.put("type", "Feature");
        bandNode.put("geometry", bandGeometry);
        bandNode.put("properties", bandProperties);

        return bandNode;
    }

    public Location asLocation() {
        if (!this.hasLocation()) {
            return null;
        }

        String stringToHash = this.getSlug();
        String sha1Hash = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(stringToHash.getBytes());
            sha1Hash = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
        }
        catch(NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        Location newLocation = new Location();
        newLocation.setId(sha1Hash);
        newLocation.setName(this.getName());
        newLocation.setSlug(this.getSlug());
        newLocation.setType(this.getSectionType());
        newLocation.setObject("Band"); // TODO use a constant
        newLocation.setPoint(new LocationPoint(this.longitude, this.latitude));

        return newLocation;
    }

    public ObjectNode asLookup(ObjectMapper objectMapper) {
        ObjectNode person = objectMapper.createObjectNode();
        person.put("slug", this.getSlug());
        person.put("name", this.name);
        person.put("context", this.getDateRange() != null ? this.getDateRange() : "");
        return person;
    }

    public String getSlugWithUnderscores() {
        return this.slug.replace("-", "_");
    }

    public boolean hasLocation() {
        return this.latitude != null && this.latitude.trim().length() > 0 && this.longitude != null && this.longitude.trim().length() > 0;
    }
}
