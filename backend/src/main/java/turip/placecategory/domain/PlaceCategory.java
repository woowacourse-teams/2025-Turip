package turip.placecategory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.category.domain.Category;
import turip.place.domain.Place;

@Entity
@Table(name = "place_category", uniqueConstraints = {
    @UniqueConstraint(name = "uq_place_category__place_id_category_id", columnNames = {"place_id", "category_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"place", "category"})
public class PlaceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false, foreignKey = @ForeignKey(name = "fk_place_category__place"))
    private Place place;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_place_category__category"))
    private Category category;

    public PlaceCategory(Place place, Category category) {
        this.place = place;
        this.category = category;
    }
}
