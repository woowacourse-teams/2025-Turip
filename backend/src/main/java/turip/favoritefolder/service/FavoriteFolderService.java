package turip.favoritefolder.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.exception.custom.ConflictException;
import turip.favoritefolder.controller.dto.request.FavoriteFolderRequest;
import turip.favoritefolder.controller.dto.response.FavoriteFolderResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFolderWithPlaceCountResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.favoriteplace.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class FavoriteFolderService {

    private final FavoriteFolderRepository favoriteFolderRepository;
    private final MemberRepository memberRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;

    public FavoriteFolderResponse createCustomFavoriteFolder(FavoriteFolderRequest request, String deviceFid) {
        Member member = findOrCreateMember(deviceFid);
        validateDuplicatedName(request.name(), member);

        FavoriteFolder favoriteFolder = FavoriteFolder.customFolderOf(member, request.name());
        FavoriteFolder savedFavoriteFolder = favoriteFolderRepository.save(favoriteFolder);

        return FavoriteFolderResponse.from(savedFavoriteFolder);
    }

    public FavoriteFoldersWithPlaceCountResponse findAllByDeviceFid(String deviceFid) {
        Member member = findOrCreateMember(deviceFid);
        List<FavoriteFolderWithPlaceCountResponse> favoriteFoldersWithPlaceCount = favoriteFolderRepository.findAllByMember(
                        member).stream()
                .map(favoriteFolder -> {
                    int placeCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);
                    return FavoriteFolderWithPlaceCountResponse.of(favoriteFolder, placeCount);
                })
                .toList();

        return FavoriteFoldersWithPlaceCountResponse.from(favoriteFoldersWithPlaceCount);
    }

    private Member findOrCreateMember(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> {
                    Member savedMember = memberRepository.save(new Member(deviceFid));
                    FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedMember);
                    favoriteFolderRepository.save(defaultFolder);
                    return savedMember;
                });
    }

    private void validateDuplicatedName(String folderName, Member member) {
        if (favoriteFolderRepository.existsByNameAndMember(folderName, member)) {
            throw new ConflictException("중복된 폴더 이름이 존재합니다.");
        }
    }
}
