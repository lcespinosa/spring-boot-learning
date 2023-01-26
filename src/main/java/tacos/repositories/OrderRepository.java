package tacos.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import tacos.models.TacoOrder;

import java.util.List;

public interface OrderRepository extends CrudRepository<TacoOrder, Long> {

    TacoOrder save(TacoOrder tacoOrder);

    List<TacoOrder> findByDeliveryNameOrderByCreatedAtDesc(String deliveryName, Pageable pageable);
}
