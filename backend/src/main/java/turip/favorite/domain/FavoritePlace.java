package turip.favorite.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.place.domain.Place;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "favorite_place", uniqueConstraints = {
        @UniqueConstraint(name = "uq_favorite_place__folder_place", columnNames = {"favorite_folder_id", "place_id"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoritePlace {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_folder_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_place__folder"))
    private FavoriteFolder favoriteFolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_place__place"))
    private Place place;

    private Integer favoriteOrder;

    public FavoritePlace(FavoriteFolder favoriteFolder, Place place, Integer favoriteOrder) {
        this.favoriteFolder = favoriteFolder;
        this.place = place;
        this.favoriteOrder = favoriteOrder;
    }

    public void updateFavoriteOrder(Integer favoriteOrder) {
        this.favoriteOrder = favoriteOrder;
    }
}
