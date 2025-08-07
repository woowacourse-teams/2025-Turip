package turip.favorite.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.favorite.domain.Favorite;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@DataJpaTest
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private CityRepository cityRepository;


    @Test
    @DisplayName("컨텐츠를 최신 찜 기준으로 정렬하여 반환한다")
    void findMyFavoriteContentsByDeviceFid() {
        // given
        Creator creator = creatorRepository.save(new Creator(null, null));
        City city = cityRepository.save(new City(null, null, null, null));

        Content content1 = contentRepository.save(new Content(creator, city, null, null, null));
        Content content2 = contentRepository.save(new Content(creator, city, null, null, null));

        Member member = memberRepository.save(new Member("testDeviceFid"));

        Favorite favorite1 = new Favorite(LocalDate.now().minusDays(2), member, content1);
        Favorite favorite2 = new Favorite(LocalDate.now().minusDays(1), member, content2); // 최신 찜 컨텐츠

        favoriteRepository.save(favorite1);
        favoriteRepository.save(favorite2);

        // when
        Slice<Content> result = favoriteRepository.findMyFavoriteContentsByDeviceFid(
                member.getDeviceFid(), Long.MAX_VALUE, PageRequest.of(0, 10));
        List<Content> contents = result.getContent();

        // then
        assertThat(contents.get(0).getId()).isEqualTo(content2.getId());
        assertThat(contents.get(1).getId()).isEqualTo(content1.getId());
    }
}
