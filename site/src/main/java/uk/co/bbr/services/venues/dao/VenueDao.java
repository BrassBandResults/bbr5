package uk.co.bbr.services.venues.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.services.map.dto.LocationPoint;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.regions.dao.RegionDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Entity
@NoArgsConstructor
@Table(name="venue")
public class VenueDao extends AbstractDao implements NameTools {
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="region_id")
    @Setter
    private RegionDao region;

    @Column(name="longitude")
    private String longitude;

    @Column(name="latitude")
    private String latitude;

    @Column(name="notes")
    private String notes;

    @Column(name="exact")
    @Setter
    private boolean exact;

    @Column(name="mapped_by")
    @Setter
    private String mapper;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="parent_id")
    @Setter
    private VenueDao parent;

    @Transient
    @Setter
    private int eventCount;

    public void setName(String name){
        this.name = simplifyVenueName(name);
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.slug = value;
    }

    public void setOldId(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.oldId = value;
    }

    public void setNotes(String notes) {
        if (notes != null) {
            notes = notes.trim();
        }
        this.notes = notes;
    }

    public void setLatitude(String latitude) {
        if (latitude != null) {
            if (latitude.trim().length() > 15) {
                latitude = latitude.trim().substring(0, 15);
            }
            latitude = latitude.trim();
        }
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        if (longitude != null) {
            if (longitude.trim().length() > 15) {
                longitude = longitude.trim().substring(0, 15);
            }
            longitude = longitude.trim();
        }
        this.longitude = longitude;
    }

    public ObjectNode asLookup(ObjectMapper objectMapper) {
        ObjectNode venue = objectMapper.createObjectNode();
        venue.put("slug", this.getSlug());
        venue.put("name", this.escapeJson(this.name));
        venue.put("context", "");
        return venue;
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
        newLocation.setType(this.exact ? "exact" : "area");
        newLocation.setObject("Venue"); // TODO use a constant
        newLocation.setPoint(new LocationPoint(this.longitude, this.latitude));

        return newLocation;
    }

    public boolean hasLocation() {
        return this.latitude != null && this.latitude.trim().length() > 0 && this.longitude != null && this.longitude.trim().length() > 0;
    }

    public ObjectNode asGeoJson(ObjectMapper objectMapper) {
        ObjectNode venueGeometry = objectMapper.createObjectNode();
        venueGeometry.put("type", "Point");
        venueGeometry.putArray("coordinates").add(Float.parseFloat(this.getLongitude())).add(Float.parseFloat(this.getLatitude()));

        ObjectNode venueProperties = objectMapper.createObjectNode();
        venueProperties.put("name", this.getName());
        venueProperties.put("slug", this.getSlug());
        venueProperties.put("offset", "venues");
        venueProperties.put("type", "venue");

        ObjectNode venueNode = objectMapper.createObjectNode();
        venueNode.put("type", "Feature");
        venueNode.put("geometry", venueGeometry);
        venueNode.put("properties", venueProperties);

        return venueNode;
    }
}
