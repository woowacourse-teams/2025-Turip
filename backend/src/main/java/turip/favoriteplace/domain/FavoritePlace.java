package turip.favoriteplace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import turip.domain.TimeStamp;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.place.domain.Place;

@Getter
@Entity
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is Null")
public class FavoritePlace extends TimeStamp {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FavoriteFolder favoriteFolder;

    @ManyToOne
    private Place place;

    public FavoritePlace(FavoriteFolder favoriteFolder, Place place) {
        this.favoriteFolder = favoriteFolder;
        this.place = place;
    }

    @PreRemove
    public void preRemove() {
        this.setDeletedAt(LocalDateTime.now());
    }
}
