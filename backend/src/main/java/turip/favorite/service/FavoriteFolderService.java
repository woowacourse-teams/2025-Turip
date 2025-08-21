package turip.favorite.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithPlaceCountResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoriteFolderService {

    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public FavoriteFolderResponse createCustomFavoriteFolder(FavoriteFolderRequest request, Member member) {
        FavoriteFolder favoriteFolder = FavoriteFolder.customFolderOf(member, request.name());

        validateDuplicatedName(favoriteFolder.getName(), member);
        FavoriteFolder savedFavoriteFolder = favoriteFolderRepository.save(favoriteFolder);

        return FavoriteFolderResponse.from(savedFavoriteFolder);
    }

    public FavoriteFoldersWithPlaceCountResponse findAllByMember(Member member) {
        List<FavoriteFolderWithPlaceCountResponse> favoriteFoldersWithPlaceCount = favoriteFolderRepository.findAllByMemberOrderByIdAsc(
                        member).stream()
                .map(favoriteFolder -> {
                    int placeCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);
                    return FavoriteFolderWithPlaceCountResponse.of(favoriteFolder, placeCount);
                })
                .toList();

        return FavoriteFoldersWithPlaceCountResponse.from(favoriteFoldersWithPlaceCount);
    }

    public FavoriteFoldersWithFavoriteStatusResponse findAllWithFavoriteStatusByDeviceId(Member member, Long placeId) {
        Place place = getPlaceById(placeId);

        List<FavoriteFolderWithFavoriteStatusResponse> favoriteFoldersWithFavoriteStatus = favoriteFolderRepository.findAllByMemberOrderByIdAsc(
                        member).stream()
                .map(favoriteFolder -> {
                    boolean isFavoritePlace = favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder,
                            place);
                    return FavoriteFolderWithFavoriteStatusResponse.of(favoriteFolder, isFavoritePlace);
                })
                .toList();

        return FavoriteFoldersWithFavoriteStatusResponse.from(favoriteFoldersWithFavoriteStatus);
    }

    @Transactional
    public FavoriteFolderResponse updateName(Member member, Long favoriteFolderId,
                                             FavoriteFolderNameRequest request) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException("기본 폴더는 수정할 수 없습니다.");
        }

        String newName = FavoriteFolder.formatName(request.name());
        validateOwnership(member, favoriteFolder);
        validateDuplicatedName(newName, member);
        favoriteFolder.rename(newName);

        return FavoriteFolderResponse.from(favoriteFolder);
    }

    @Transactional
    public void remove(Member member, Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);

        if (favoriteFolder.isDefault()) {
            throw new BadRequestException("기본 폴더는 삭제할 수 없습니다.");
        }
        validateOwnership(member, favoriteFolder);

        favoriteFolderRepository.deleteById(favoriteFolderId);
    }

    private void validateDuplicatedName(String folderName, Member member) {
        if (favoriteFolderRepository.existsByNameAndMember(folderName, member)) {
            throw new ConflictException("중복된 폴더 이름이 존재합니다.");
        }
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 장소가 존재하지 않습니다."));
    }

    private FavoriteFolder getById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 폴더가 존재하지 않습니다."));
    }

    private void validateOwnership(Member requestMember, FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isOwner(requestMember)) {
            throw new ForbiddenException("폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다.");
        }
    }
}
