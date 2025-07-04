package vine.vine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vine.vine.domain.Charges;

import java.util.List;

@Repository
public interface ChargesRepository extends JpaRepository<Charges, Long> {
    List<Charges> findByBookId(Long bookId);
}
