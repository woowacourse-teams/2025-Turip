package turip.content.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findByTitleAndUrl(String title, String url);

    @Query("""
            SELECT COUNT(c) FROM Content c
            WHERE c.city.name = :cityName
            """)
    int countByCityName(@Param("cityName") String cityName);

    @Query("""
            SELECT COUNT(c) FROM Content c
            WHERE c.city.country.name = :countryName
            """)
    int countByCityCountryName(@Param("countryName") String countryName);

    @Query("""
            SELECT COUNT(c) FROM Content c
            JOIN c.city ct
            JOIN ct.country co
            WHERE ct.name NOT IN :cityNames AND co.name = '대한민국'
            """)
    int countDomesticEtcContents(@Param("cityNames") List<String> cityNames);

    @Query("""
            SELECT COUNT(c) FROM Content c
            JOIN c.city ct
            JOIN ct.country co
            WHERE co.name NOT IN :countryNames AND co.name != '대한민국'
            """)
    int countOverseasEtcContents(@Param("countryNames") List<String> countryNames);

    @Query("""
            SELECT COUNT(DISTINCT c) FROM Content c
             JOIN c.creator cr
             LEFT JOIN ContentPlace cp ON c.id = cp.content.id
             LEFT JOIN cp.place p
             WHERE c.title LIKE %:keyword%
                OR cr.channelName LIKE %:keyword%
                OR p.name LIKE %:keyword%
            """)
    int countByKeywordContaining(@Param("keyword") String keyword);

    @Query("""
            SELECT c FROM Content c
            WHERE c.city.name = :cityName AND c.id < :lastId
            ORDER BY c.id DESC
            """)
    Slice<Content> findByCityName(@Param("cityName") String cityName, @Param("lastId") Long lastId, Pageable pageable);

    @Query("""
            SELECT c FROM Content c
            WHERE c.city.country.name = :countryName AND c.id < :lastId
            ORDER BY c.id DESC
            """)
    Slice<Content> findByCityCountryName(@Param("countryName") String countryName, @Param("lastId") Long lastId,
                                         Pageable pageable);

    @Query("""
            SELECT c FROM Content c JOIN c.city ct JOIN ct.country co
            WHERE ct.name NOT IN :domesticCategoryNames AND co.name = '대한민국' AND c.id < :lastId
            ORDER BY c.id DESC
            """)
    Slice<Content> findDomesticEtcContents(@Param("domesticCategoryNames") List<String> domesticCategoryNames,
                                           @Param("lastId") Long lastId, Pageable pageable);

    @Query("""
            SELECT c FROM Content c JOIN c.city ct JOIN ct.country co
            WHERE co.name NOT IN :overseasCategoryNames AND co.name != '대한민국' AND c.id < :lastId
            ORDER BY c.id DESC
            """)
    Slice<Content> findOverseasEtcContents(@Param("overseasCategoryNames") List<String> overseasCategoryNames,
                                           @Param("lastId") Long lastId, Pageable pageable);

    @Query("""
            SELECT DISTINCT c FROM Content c
            JOIN c.creator cr
            LEFT JOIN ContentPlace cp ON c.id = cp.content.id
            LEFT JOIN cp.place p
            WHERE c.id < :lastId
            AND (c.title LIKE %:keyword%
                 OR cr.channelName LIKE %:keyword%
                 OR p.name LIKE %:keyword%)
            ORDER BY c.id DESC
            """)
    Slice<Content> findByKeywordContaining(@Param("keyword") String keyword, @Param("lastId") Long lastId,
                                           Pageable pageable);
}
