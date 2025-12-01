package turip.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.member.domain.Guest;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByDeviceFid(String deviceFid);
}
