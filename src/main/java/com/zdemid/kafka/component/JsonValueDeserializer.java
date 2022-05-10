package com.zdemid.kafka.component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdemid.kafka.conditional.ConditionalOnJsonValueDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

@Component
@ConditionalOnJsonValueDeserializer
@RequiredArgsConstructor
public class JsonValueDeserializer extends JsonDeserializer<Object> {

    private final ObjectMapper objectMapper;
    private final Map<String, List<JavaType>> topicClassMap = new HashMap<>();

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        var availableTypes = topicClassMap.get(topic);
        Assert.state(availableTypes != null && !availableTypes.isEmpty(), "No default type provided for topic " + topic);
        return availableTypes.stream()
                .map(type -> convertData(data, type))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DeserializationException("Can't deserialize data [" + Arrays.toString(data) + "] from topic [" + topic + "]", data, false, null));
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return deserialize(topic, null, data);
    }

    public void add(String topic, JavaType type) {
        topicClassMap.compute(topic, (k, l) -> {
            if (l == null) {
                var newList = new ArrayList<JavaType>();
                newList.add(type);
                return newList;
            } else {
                l.add(type);
                return l;
            }
        });
    }

    private Object convertData(byte[] data, JavaType type) {
        try {
            return objectMapper.readerFor(type).readValue(data);
        } catch (IOException e) {
            return null;
        }
    }

}
