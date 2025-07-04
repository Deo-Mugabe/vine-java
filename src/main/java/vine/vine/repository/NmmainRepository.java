package vine.vine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vine.vine.domain.Nmmain;

@Repository
public interface NmmainRepository extends JpaRepository<Nmmain, Long> {
}
