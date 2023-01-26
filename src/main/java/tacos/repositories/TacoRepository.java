package tacos.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import tacos.models.Taco;

import java.util.Optional;

@Repository
public interface TacoRepository extends PagingAndSortingRepository<Taco, Long> {

    Taco save(Taco taco);

    Optional<Taco> findById(Long id);
}
