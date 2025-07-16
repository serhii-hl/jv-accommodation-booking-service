package app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "accommodations")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType type;
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private Set<AccommodationPhoto> photos = new HashSet<>();
    @OneToOne(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationSize size;
    @ElementCollection(targetClass = Amenity.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "accommodation_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "amenity")
    @BatchSize(size = 25)
    private Set<Amenity> amenitySet;
    @Column(nullable = false)
    private BigDecimal dailyPrice;
    @Column(nullable = false)
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private Set<AccommodationUnit> units = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
