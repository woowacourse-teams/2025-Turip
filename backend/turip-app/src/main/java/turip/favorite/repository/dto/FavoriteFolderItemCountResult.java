package turip.favorite.repository.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record FavoriteFolderItemCountResult(
        Long favoriteFolderId,
        Long count
) {

    public static Map<Long, Long> toCountMap(List<FavoriteFolderItemCountResult> results) {
        return results.stream()
                .collect(Collectors.toMap(
                        FavoriteFolderItemCountResult::favoriteFolderId,
                        FavoriteFolderItemCountResult::count
                ));
    }
}
