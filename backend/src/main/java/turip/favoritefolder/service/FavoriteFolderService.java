package turip.favoritefolder.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.exception.custom.ConflictException;
import turip.exception.custom.ForbiddenException;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.controller.dto.request.FavoriteFolderNameRequest;
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

    @Transactional
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

    @Transactional
    public FavoriteFolderResponse updateName(String deviceFid, Long favoriteFolderId,
                                             FavoriteFolderNameRequest request) {
        Member member = getMemberByDeviceId(deviceFid);
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);

        validateOwnership(member, favoriteFolder);
        validateDuplicatedName(request.name(), member);

        favoriteFolder.setName(request.name());

        return FavoriteFolderResponse.from(favoriteFolder);
    }

    @Transactional
    public void remove(String deviceFid, Long favoriteFolderId) {
        Member member = getMemberByDeviceId(deviceFid);
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);

        validateOwnership(member, favoriteFolder);

        favoriteFolderRepository.deleteById(favoriteFolderId);
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

    private FavoriteFolder getById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 폴더가 존재하지 않습니다."));
    }

    private Member getMemberByDeviceId(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 회원이 존재하지 않습니다."));
    }

    private void validateOwnership(Member requestMember, FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isOwner(requestMember)) {
            throw new ForbiddenException("폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다.");
        }
    }
}
