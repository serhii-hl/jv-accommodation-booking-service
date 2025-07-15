package app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "locations")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "UPDATE locations SET is_deleted = true WHERE accommodation_id=?")
@SQLRestriction("is_deleted = false")
public class Location {
    @Id
    private long id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String number;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
