package tacos.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tacos.models.IngredientRef;
import tacos.models.Taco;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcTacoRepository implements TacoRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTacoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Taco save(Taco taco) {
        return this.save(null, -1, taco);
    }

    @Override
    @Transactional
    public Taco save(Long tacoOrderId, int tacoOrderKey, Taco taco) {
        PreparedStatementCreatorFactory preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                "insert into Taco (name, created_at, taco_order, taco_order_key) values (?, ?, ?, ?)",
                Types.VARCHAR, Types.TIMESTAMP, Types.BIGINT, Types.BIGINT
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);

        taco.setCreatedAt(new Date());
        PreparedStatementCreator preparedStatementCreator = preparedStatementCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(
                        taco.getName(),
                        taco.getCreatedAt(),
                        tacoOrderId,
                        tacoOrderKey
                )
        );

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(preparedStatementCreator, generatedKeyHolder);
        long tacoId = generatedKeyHolder.getKey().longValue();
        taco.setId(tacoId);

        saveIngredientRef(tacoId, taco.getIngredients());

        return taco;
    }

    private void saveIngredientRef(long tacoId, List<IngredientRef> ingredients) {
        int index = 0;
        for (IngredientRef ingredient :
                ingredients) {
            this.jdbcTemplate.update(
                    "insert into Ingredient_Ref (ingredient, taco, taco_key) values (?, ?, ?)",
                    ingredient.getIngredient(), tacoId, index++
            );
        }
    }
}
