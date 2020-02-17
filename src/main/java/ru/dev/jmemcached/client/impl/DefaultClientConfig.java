package ru.dev.jmemcached.client.impl;

import ru.dev.jmemcached.client.ClientConfig;
import ru.dev.jmemcached.common.protocol.ObjectSerializer;
import ru.dev.jmemcached.common.protocol.RequestConverter;
import ru.dev.jmemcached.common.protocol.ResponseConverter;
import ru.dev.jmemcached.common.protocol.impl.DefaultObjectSerializer;
import ru.dev.jmemcached.common.protocol.impl.DefaultRequestConverter;
import ru.dev.jmemcached.common.protocol.impl.DefaultResponseConverter;

class DefaultClientConfig implements ClientConfig {

    private final String host;
    private final int port;
    private final RequestConverter requestConverter;
    private final ResponseConverter responseConverter;
    private final ObjectSerializer objectSerializer;

    DefaultClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
        this.requestConverter = new DefaultRequestConverter();
        this.responseConverter = new DefaultResponseConverter();
        this.objectSerializer = new DefaultObjectSerializer();
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public RequestConverter getRequestConverter() {
        return requestConverter;
    }

    @Override
    public ResponseConverter getResponseConverter() {
        return responseConverter;
    }

    @Override
    public ObjectSerializer getObjectSerializer() {
        return objectSerializer;
    }

}
