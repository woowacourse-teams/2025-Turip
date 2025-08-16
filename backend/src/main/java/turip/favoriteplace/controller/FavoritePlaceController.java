package turip.favoriteplace.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.favoriteplace.controller.dto.response.FavoritePlaceResponse;
import turip.favoriteplace.service.FavoritePlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-places")
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;

    @PostMapping
    public ResponseEntity<FavoritePlaceResponse> create(
            @RequestHeader("device-fid") String deviceFid,
            @RequestParam("favoriteFolderId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {

        FavoritePlaceResponse response = favoritePlaceService.create(deviceFid, favoriteFolderId, placeId);
        return ResponseEntity.created(URI.create("/favorite-places/" + response.id()))
                .body(response);
    }
}
