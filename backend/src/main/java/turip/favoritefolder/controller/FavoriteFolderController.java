package turip.favoritefolder.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.favoritefolder.controller.dto.request.FavoriteFolderRequest;
import turip.favoritefolder.controller.dto.response.FavoriteFolderResponse;
import turip.favoritefolder.service.FavoriteFolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-folders")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;

    @PostMapping
    public ResponseEntity<FavoriteFolderResponse> create(@RequestHeader("device-fid") String deviceFid,
                                                         @RequestBody FavoriteFolderRequest request) {
        FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, deviceFid);
        return ResponseEntity.created(URI.create("/favorite-folders/" + response.id()))
                .body(response);
    }
}
