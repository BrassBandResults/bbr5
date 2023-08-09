package uk.co.bbr.services.map.dto;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Id;

@Getter
@Setter
@Container(containerName = "locations")
public class Location {

    @Id
    private String id;
    private String object;  // "Band" or "Venue"
    private String name;
    @PartitionKey
    private String slug;
    private String type; // type, e.g. status.extinct or section.first
    private LocationPoint point;

    public ObjectNode asGeoJson(ObjectMapper objectMapper) {
        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");
        geometry.putArray("coordinates").add(this.point.getLongitude()).add(this.point.getLatitude());

        ObjectNode locationProperties = objectMapper.createObjectNode();
        locationProperties.put("name", this.getName());
        locationProperties.put("slug", this.getSlug());
        if ("Venue".equals(this.object)) {
            locationProperties.put("type", "venue");
            locationProperties.put("offset", "venues");
        } else {
            locationProperties.put("type", this.type);
            locationProperties.put("offset", "bands");
        }

        ObjectNode bandNode = objectMapper.createObjectNode();
        bandNode.put("type", "Feature");
        bandNode.put("geometry", geometry);
        bandNode.put("properties", locationProperties);

        return bandNode;
    }
}
