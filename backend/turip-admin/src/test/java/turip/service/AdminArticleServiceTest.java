package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.account.domain.TuripMember;
import turip.article.domain.Article;
import turip.article.domain.ArticlePlace;
import turip.article.domain.ArticleTag;
import turip.article.domain.Tag;
import turip.article.repository.ArticlePlaceRepository;
import turip.article.repository.ArticleRepository;
import turip.article.repository.ArticleTagRepository;
import turip.article.repository.TagRepository;
import turip.controller.dto.request.AdminArticleCreateRequest;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.MemberFixture;
import turip.util.fixture.PlaceFixture;
import turip.util.fixture.TuripMemberFixture;

@ExtendWith(MockitoExtension.class)
class AdminArticleServiceTest {

    @InjectMocks
    private AdminArticleService adminArticleService;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleTagRepository articleTagRepository;
    @Mock
    private ArticlePlaceRepository articlePlaceRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PlaceRepository placeRepository;

    private TuripMember admin;

    @BeforeEach
    void setUp() {
        Account account = AccountFixture.createAdmin();
        admin = TuripMemberFixture.createTuripMember();
        ReflectionTestUtils.setField(admin, "member", MemberFixture.createCustomMember(account, "admin@turip.com", false));
    }

    @Test
    @DisplayName("태그와 연관 장소 없이 아티클을 생성한다.")
    void create1() {
        // given
        AdminArticleCreateRequest request = new AdminArticleCreateRequest(
                "제목", "부제목", "본문", null, false, List.of(), List.of()
        );

        when(articleRepository.findMinDisplayOrder()).thenReturn(Optional.empty());
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        });

        // when
        Long articleId = adminArticleService.create(request, admin);

        // then
        assertThat(articleId).isEqualTo(1L);
    }

    @Test
    @DisplayName("기존 태그는 재사용하고, 없는 태그는 새로 생성해서 연결한다. 태그 조회는 한번의 배치 쿼리로 수행한다.")
    void create2() {
        // given
        AdminArticleCreateRequest request = new AdminArticleCreateRequest(
                "제목", "부제목", "본문", null, false, List.of("여행", "행복"), List.of()
        );

        Tag existingTag = new Tag("여행");
        ReflectionTestUtils.setField(existingTag, "id", 10L);

        when(articleRepository.findMinDisplayOrder()).thenReturn(Optional.empty());
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        });
        when(tagRepository.findAllByNameIn(List.of("여행", "행복"))).thenReturn(List.of(existingTag));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            ReflectionTestUtils.setField(tag, "id", 11L);
            return tag;
        });

        // when
        adminArticleService.create(request, admin);

        // then
        verify(tagRepository, never()).findByName(any(String.class));
        verify(tagRepository, times(1)).findAllByNameIn(List.of("여행", "행복"));
        verify(tagRepository, never()).save(existingTag);
        verify(tagRepository, times(1)).save(argThat(tag -> tag.getName().equals("행복")));
        verify(articleTagRepository, times(2)).save(any(ArticleTag.class));
    }

    @Test
    @DisplayName("존재하지 않는 placeId는 무시하고, 존재하는 place만 연결한다.")
    void create3() {
        // given
        AdminArticleCreateRequest request = new AdminArticleCreateRequest(
                "제목", "부제목", "본문", null, false, List.of(), List.of(1L, 999L)
        );

        Place existingPlace = PlaceFixture.createWithId(1L);

        when(articleRepository.findMinDisplayOrder()).thenReturn(Optional.empty());
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        });
        when(placeRepository.findAllById(List.of(1L, 999L))).thenReturn(List.of(existingPlace));

        // when
        adminArticleService.create(request, admin);

        // then
        verify(articlePlaceRepository, times(1)).save(any(ArticlePlace.class));
    }
}
