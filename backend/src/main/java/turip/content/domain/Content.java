package turip.content.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.creator.domain.Creator;
import turip.region.domain.Region;

@Getter
@Entity
@AllArgsConstructor
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
    private Region region;

    private String title;

    private String url;

    private LocalDate uploadedDate;

    public Content(
            Creator creator,
            Region region,
            String title,
            String url,
            LocalDate uploadedDate
    ) {
        this.creator = creator;
        this.region = region;
        this.title = title;
        this.url = url;
        this.uploadedDate = uploadedDate;
    }
}
