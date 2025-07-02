package vine.vine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vine.vine.domain.Jrelease;

@Repository
public interface JreleaseRepository extends JpaRepository<Jrelease, Integer> {
}
