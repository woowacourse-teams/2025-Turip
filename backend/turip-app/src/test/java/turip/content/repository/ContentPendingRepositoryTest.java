package turip.content.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.container.TestContainerConfig;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.domain.ContentPendingStatus;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test-mysql")
class ContentPendingRepositoryTest extends TestContainerConfig {

    @Autowired
    private ContentPendingRepository contentPendingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clear() {
        jdbcTemplate.execute("DELETE FROM content_pending");
        jdbcTemplate.execute("DELETE FROM account");
    }

    @DisplayName("동일한 URL과 상태를 가진 콘텐츠가 존재하는지 확인한다")
    @Test
    void existsByVideoUrlAndStatus() {
        // given
        Account account = createAccount(Role.ADMIN);
        String videoUrl = "https://example.com/video1";
        ContentPendingData contentData = createContentData(videoUrl);
        ContentPending contentPending = new ContentPending(contentData, account, null, null, null);
        contentPendingRepository.save(contentPending);

        // when
        boolean exists = contentPendingRepository.existsByVideoUrlAndStatus(videoUrl, ContentPendingStatus.PENDING);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("특정 상태의 콘텐츠 목록을 ID 내림차순으로 조회한다")
    @Test
    void findAllByStatusOrderByIdDesc() {
        // given
        Account account = createAccount(Role.ADMIN);

        ContentPending pending1 = new ContentPending(createContentData("url1"), account, null, null, null);
        ContentPending pending2 = new ContentPending(createContentData("url2"), account, null, null, null);
        ContentPending rejected = new ContentPending(createContentData("url3"), account, null, null, null);
        rejected.reject(account, "거절");

        contentPendingRepository.save(pending1);
        contentPendingRepository.save(pending2);
        contentPendingRepository.save(rejected);

        // when
        List<ContentPending> results = contentPendingRepository.findAllByStatusOrderByIdDesc(
                ContentPendingStatus.PENDING);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isGreaterThan(results.get(1).getId());
    }

    @DisplayName("수집자 계정으로 콘텐츠 목록을 ID 내림차순으로 조회한다")
    @Test
    void findAllByCollectorAccountOrderByIdDesc() {
        // given
        Account collector1 = createAccount(Role.ADMIN, "test-user1");
        Account collector2 = createAccount(Role.ADMIN, "test-user2");

        ContentPending cp1 = new ContentPending(createContentData("url1"), collector1, null, null, null);
        ContentPending cp2 = new ContentPending(createContentData("url2"), collector1, null, null, null);
        ContentPending cp3 = new ContentPending(createContentData("url3"), collector2, null, null, null);

        contentPendingRepository.save(cp1);
        contentPendingRepository.save(cp2);
        contentPendingRepository.save(cp3);

        // when
        List<ContentPending> results = contentPendingRepository.findAllByCollectorAccountOrderByIdDesc(collector1);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isGreaterThan(results.get(1).getId());
    }

    @DisplayName("특정 상태의 콘텐츠 개수를 조회한다")
    @Test
    void countByStatus() {
        // given
        Account account = createAccount(Role.ADMIN);

        ContentPending pending1 = new ContentPending(createContentData("url1"), account, null, null, null);
        ContentPending pending2 = new ContentPending(createContentData("url2"), account, null, null, null);
        ContentPending rejected = new ContentPending(createContentData("url3"), account, null, null, null);
        rejected.reject(account, "거절");

        contentPendingRepository.save(pending1);
        contentPendingRepository.save(pending2);
        contentPendingRepository.save(rejected);

        // when
        long pendingCount = contentPendingRepository.countByStatus(ContentPendingStatus.PENDING);
        long rejectedCount = contentPendingRepository.countByStatus(ContentPendingStatus.REJECTED);

        // then
        assertThat(pendingCount).isEqualTo(2);
        assertThat(rejectedCount).isEqualTo(1);
    }

    private Account createAccount(Role role) {
        return createAccount(role, "test_user");
    }

    private Account createAccount(Role role, String nickName) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO account (role, nickname) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.name());
            ps.setString(2, nickName);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        Account account = new Account(role, "test_user");
        ReflectionTestUtils.setField(account, "id", id);
        return account;
    }

    private ContentPendingData createContentData(String videoUrl) {
        ContentPendingData.VideoData videoData = new ContentPendingData.VideoData(
                "videoId",
                "Test Video",
                videoUrl,
                "Test Channel",
                "http://image.url",
                LocalDate.now()
        );

        ContentPendingData.PlaceData placeData = new ContentPendingData.PlaceData(
                "Test Place",
                "http://place.url",
                "Test Address",
                37.5665,
                126.9780,
                "CAFE"
        );

        ContentPendingData.ContentPlaceData contentPlaceData = new ContentPendingData.ContentPlaceData(
                1,
                1,
                "09:00",
                placeData
        );

        return new ContentPendingData("Seoul", videoData, List.of(contentPlaceData));
    }
}
