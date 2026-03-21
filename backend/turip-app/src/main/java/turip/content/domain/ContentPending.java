package turip.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalStateException;

@Getter
@Entity
@Table(name = "content_pending")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentPending {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_data", nullable = false, columnDefinition = "JSON")
    private String contentData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentPendingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collector_account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_pending__collector_account"))
    private Account collectorAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_account_id", foreignKey = @ForeignKey(name = "fk_content_pending__validator_account"))
    private Account validatorAccount;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", foreignKey = @ForeignKey(name = "fk_content_pending__content"))
    private Content content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ContentPending(
            String contentData,
            ContentPendingStatus status,
            Account collectorAccount,
            Account validatorAccount,
            String rejectReason,
            Content content
    ) {
        this.contentData = contentData;
        this.status = status;
        this.collectorAccount = collectorAccount;
        this.validatorAccount = validatorAccount;
        this.rejectReason = rejectReason;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void approve(Account validatorAccount, Content content) {
        validateStatusNotApproved();

        this.status = ContentPendingStatus.APPROVED;
        this.validatorAccount = validatorAccount;
        this.rejectReason = "";
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(Account validatorAccount, String rejectReason) {
        validateStatusNotApproved();

        this.status = ContentPendingStatus.REJECTED;
        this.validatorAccount = validatorAccount;
        this.rejectReason = rejectReason;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateStatusNotApproved() {
        if (this.status == ContentPendingStatus.APPROVED) {
            throw new IllegalStateException(ErrorTag.IS_ALREADY_APPROVED);
        }
    }
}
