package io.quarkusdroneshop.qdca10.infrastructure;

import io.quarkusdroneshop.qdca10.domain.Qdca10;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;

import java.util.function.Supplier;

public class Qdca10Task implements Supplier<Qdca10Result> {
    private final Qdca10 qdca10;
    private final OrderIn orderIn;

    public Qdca10Task(Qdca10 qdca10, OrderIn orderIn) {
        this.qdca10 = qdca10;
        this.orderIn = orderIn;
    }

    @Override
    public Qdca10Result get() {
        return qdca10.make(orderIn);
    }
}