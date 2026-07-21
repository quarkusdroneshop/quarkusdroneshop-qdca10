package io.quarkusdroneshop.qdca10.infrastructure;

import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.qdca10.domain.valueobjects.ComponentStockUpdate;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * drone-component-stock データプロダクトが公開する dataproduct-component-stock-quantity
 * (upsert-kafka, Avro) を ComponentStockUpdate に変換する。QDCA10 が扱わない item
 * (QDCA10pro 側の品目等が将来追加された場合など) や変換に失敗したレコードは
 * null を返し、KafkaService 側で無視される。
 */
public class ComponentStockQuantityDeserializer implements Deserializer<ComponentStockUpdate> {

    private static final Logger logger = LoggerFactory.getLogger(ComponentStockQuantityDeserializer.class);

    private final AvroKafkaDeserializer<GenericRecord> avroDeserializer = new AvroKafkaDeserializer<>();

    @Override
    public void configure(java.util.Map<String, ?> configs, boolean isKey) {
        avroDeserializer.configure(configs, isKey);
    }

    @Override
    public ComponentStockUpdate deserialize(String topic, byte[] data) {
        if (data == null) {
            // upsert-kafka のトゥームストーン (削除) は在庫判定上は無視する。
            return null;
        }

        GenericRecord record = avroDeserializer.deserialize(topic, new RecordHeaders(), data);
        if (record == null) {
            return null;
        }

        try {
            Item item = Item.valueOf(record.get("item").toString());
            long quantity = (Long) record.get("quantity");
            return new ComponentStockUpdate(item, quantity);
        } catch (IllegalArgumentException e) {
            // QDCA10pro 専用品目など、この Item enum に存在しないコードは無視する。
            logger.debug("Unknown item in component-stock-quantity record, skipping: {}", record);
            return null;
        } catch (Exception e) {
            logger.warn("Failed to convert component-stock-quantity record: {}", record, e);
            return null;
        }
    }

    @Override
    public void close() {
        avroDeserializer.close();
    }
}
