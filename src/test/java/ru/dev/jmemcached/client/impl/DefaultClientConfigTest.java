package ru.dev.jmemcached.client.impl;

import org.junit.Test;
import ru.dev.jmemcached.common.protocol.impl.DefaultObjectSerializer;
import ru.dev.jmemcached.common.protocol.impl.DefaultRequestConverter;
import ru.dev.jmemcached.common.protocol.impl.DefaultResponseConverter;

import static org.junit.Assert.assertEquals;

public class DefaultClientConfigTest {
    private final DefaultClientConfig defaultClientConfig = new DefaultClientConfig("localhost", 9010);

    @Test
    public void getHost() {
        assertEquals("localhost", defaultClientConfig.getHost());
    }

    @Test
    public void getPort() {
        assertEquals(9010, defaultClientConfig.getPort());
    }

    @Test
    public void getRequestConverter() {
        assertEquals(DefaultRequestConverter.class, defaultClientConfig.getRequestConverter().getClass());
    }

    @Test
    public void getResponseConverter() {
        assertEquals(DefaultResponseConverter.class, defaultClientConfig.getResponseConverter().getClass());
    }

    @Test
    public void getObjectSerializer() {
        assertEquals(DefaultObjectSerializer.class, defaultClientConfig.getObjectSerializer().getClass());
    }
}
