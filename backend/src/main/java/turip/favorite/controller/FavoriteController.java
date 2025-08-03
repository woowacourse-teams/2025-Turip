package turip.favorite.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.favorite.FavoriteService;
import turip.favorite.controller.dto.request.FavoriteRequest;
import turip.favorite.controller.dto.response.FavoriteResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponse> create(@RequestHeader("device-fid") String deviceFid,
                                                   @RequestBody FavoriteRequest request) {
        FavoriteResponse response = favoriteService.create(request, deviceFid);
        return ResponseEntity.created(URI.create("/favorites/" + response.id()))
                .body(response);
    }
}
