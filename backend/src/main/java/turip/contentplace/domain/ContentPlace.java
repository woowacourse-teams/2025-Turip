package turip.contentplace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.content.domain.Content;
import turip.place.domain.Place;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentPlace {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int visitDay;

    private int visitOrder;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Content content;

    public ContentPlace(int visitDay, int visitOrder, Place place, Content content) {
        this.visitDay = visitDay;
        this.visitOrder = visitOrder;
        this.place = place;
        this.content = content;
    }
}
