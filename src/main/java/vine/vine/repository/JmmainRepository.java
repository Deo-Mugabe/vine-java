package vine.vine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vine.vine.domain.Jmmain;

@Repository
public interface JmmainRepository extends JpaRepository<Jmmain, Long> {
}
