package turip.content.domain;

import jakarta.persistence.Column;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.creator.domain.Creator;
import turip.region.domain.City;

@Getter
@Entity
@Table(name = "content", uniqueConstraints = {
        @UniqueConstraint(name = "uq_content_creator_title", columnNames = {"creator_id", "title"})
})
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_creator"))
    private Creator creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_city"))
    private City city;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "url", nullable = false, length = 65535)
    private String url;

    @Column(name = "uploaded_date", nullable = false)
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
