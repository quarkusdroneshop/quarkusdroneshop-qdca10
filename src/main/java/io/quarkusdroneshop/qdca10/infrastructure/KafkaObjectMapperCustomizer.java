package io.quarkusdroneshop.qdca10.infrastructure;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public class KafkaObjectMapperCustomizer implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        // 日付を ISO-8601 に出力（例: 2025-07-12T03:56:41.165Z）
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}