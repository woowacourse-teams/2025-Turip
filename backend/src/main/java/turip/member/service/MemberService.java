package turip.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;

    @Transactional
    public Member findOrCreateMember(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> {
                    Member savedMember = memberRepository.save(new Member(deviceFid));
                    FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedMember);
                    favoriteFolderRepository.save(defaultFolder);
                    return savedMember;
                });
    }

    public Member getMemberByDeviceId(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseThrow(() -> new NotFoundException(ErrorTag.MEMBER_NOT_FOUND));
    }
}
