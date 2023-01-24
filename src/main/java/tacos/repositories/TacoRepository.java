package tacos.repositories;

import tacos.models.Taco;

public interface TacoRepository {

    Taco save(Taco taco);
    Taco save(Long tacoOrderId, int tacoOrderKey, Taco taco);
}
