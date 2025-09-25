package turip.place.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "place")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url", nullable = false, unique = true, length = 65535)
    private String url;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceCategory> placeCategories = new ArrayList<>();

    public Place(Long id, String name, String url, String address, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place(String name, String url, String address, double latitude, double longitude) {
        this.name = name;
        this.url = url;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addCategory(Category category) {
        PlaceCategory placeCategory = new PlaceCategory(this, category);
        placeCategories.add(placeCategory);
    }
}
