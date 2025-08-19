package turip.city.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.country.domain.Country;
import turip.province.domain.Province;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Country country;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = true)
    private Province province;

    private String name;

    private String imageUrl;

    public City(Country country, Province province, String name, String imageUrl) {
        this.country = country;
        this.province = province;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
