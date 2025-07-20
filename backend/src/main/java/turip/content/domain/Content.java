package turip.content.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.creator.domain.Creator;
import turip.region.domain.Region;
import turip.tripcourse.domain.TripCourse;

@Getter
@Entity
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Creator creator;

    @ManyToOne
    private Region region;

    @OneToMany
    @JoinColumn(name = "content_id")
    private List<TripCourse> tripCourses;

    private String title;

    private String url;

    private LocalDate uploadedDate;

    public Content(Creator creator, Region region, List<TripCourse> tripCourses, String title, String url,
                   LocalDate uploadedDate) {
        this.creator = creator;
        this.region = region;
        this.tripCourses = tripCourses;
        this.title = title;
        this.url = url;
        this.uploadedDate = uploadedDate;
    }

    public int getTripCourseCount() {
        return tripCourses.size();
    }

    public int getTripDurationDays() {
        return tripCourses.stream()
                .mapToInt(TripCourse::getVisitDay)
                .max()
                .orElse(0);
    }
}
