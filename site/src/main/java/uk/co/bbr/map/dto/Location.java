package uk.co.bbr.map.dto;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

import javax.persistence.Id;

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
    private String type; // band type, e.g. status.extinct or section.first
    private LocationPoint point;
}
