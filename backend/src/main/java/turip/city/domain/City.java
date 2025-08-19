package turip.city.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.country.domain.Country;
import turip.province.domain.Province;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "city")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false, foreignKey = @ForeignKey(name = "fk_city_country"))
    private Country country;

    @ManyToOne
    @JoinColumn(name = "province_id", foreignKey = @ForeignKey(name = "fk_city_province"))
    private Province province;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url", nullable = false, length = 65535)
    private String imageUrl;

    public City(Country country, Province province, String name, String imageUrl) {
        this.country = country;
        this.province = province;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
