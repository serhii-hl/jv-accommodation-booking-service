package app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
public class Location {
    @Id
    private long id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
    private String country;
    private String city;
    private String street;
    private String number;
}
