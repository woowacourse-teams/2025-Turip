package turip.content.domain;

import jakarta.persistence.Column;
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
import turip.place.domain.Place;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "content_place", uniqueConstraints = {
        @UniqueConstraint(name = "uq_content_place__content_day_order", columnNames = {"content_id", "visit_day",
                "visit_order"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentPlace {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_day", nullable = false)
    private int visitDay;

    @Column(name = "visit_order", nullable = false)
    private int visitOrder;

    @Column(name = "time_line", nullable = false)
    private LocalTime timeLine;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_place__place"))
    private Place place;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_place__content"))
    private Content content;

    public ContentPlace(int visitDay, int visitOrder, LocalTime timeLine, Place place, Content content) {
        this.visitDay = visitDay;
        this.visitOrder = visitOrder;
        this.timeLine = timeLine;
        this.place = place;
        this.content = content;
    }
}
