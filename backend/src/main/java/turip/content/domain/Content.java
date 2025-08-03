package turip.content.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.city.domain.City;
import turip.creator.domain.Creator;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Creator creator;

    @ManyToOne
    private City city;

    private String title;

    private String url;

    private LocalDate uploadedDate;

    public Content(
            Creator creator,
            City city,
            String title,
            String url,
            LocalDate uploadedDate
    ) {
        this.creator = creator;
        this.city = city;
        this.title = title;
        this.url = url;
        this.uploadedDate = uploadedDate;
    }
}
