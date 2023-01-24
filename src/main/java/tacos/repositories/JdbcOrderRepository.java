package tacos.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tacos.models.Taco;
import tacos.models.TacoOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    private final TacoRepository tacoRepository;

    public JdbcOrderRepository(
            JdbcTemplate jdbcTemplate,
            TacoRepository tacoRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.tacoRepository = tacoRepository;
    }

    @Override
    @Transactional
    public TacoOrder save(TacoOrder tacoOrder) {
        PreparedStatementCreatorFactory preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                "insert into Taco_Order "
                        + "(deliveryName, delivery_street, delivery_city, delivery_state, "
                        + "delivery_zip, cc_number, cc_expiration, cc_cvv, created_at) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);

        tacoOrder.setCreatedAt(new Date());
        PreparedStatementCreator preparedStatementCreator = preparedStatementCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(
                        tacoOrder.getDeliveryName(),
                        tacoOrder.getDeliveryStreet(),
                        tacoOrder.getDeliveryCity(),
                        tacoOrder.getDeliveryState(),
                        tacoOrder.getDeliveryZip(),
                        tacoOrder.getCcNumber(),
                        tacoOrder.getCcExpiration(),
                        tacoOrder.getCcCVV(),
                        tacoOrder.getCreatedAt()
                )
        );

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(preparedStatementCreator, generatedKeyHolder);
        long tacoOrderId = generatedKeyHolder.getKey().longValue();
        tacoOrder.setId(tacoOrderId);

        List<Taco> tacos = tacoOrder.getTacos();
        int index = 0;
        for (Taco taco :
                tacos) {
            this.tacoRepository.save(tacoOrderId, index++, taco);
        }

        return tacoOrder;
    }
}
