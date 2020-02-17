package ru.dev.jmemcached.client.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import ru.dev.jmemcached.client.ClientConfig;
import ru.dev.jmemcached.common.protocol.ObjectSerializer;
import ru.dev.jmemcached.common.protocol.RequestConverter;
import ru.dev.jmemcached.common.protocol.ResponseConverter;
import ru.dev.jmemcached.common.protocol.model.Command;
import ru.dev.jmemcached.common.protocol.model.Request;
import ru.dev.jmemcached.common.protocol.model.Response;
import ru.dev.jmemcached.common.protocol.model.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultClientTest {

    private DefaultClient defaultClient;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ClientConfig clientConfig;
    private RequestConverter requestConverter;
    private ResponseConverter responseConverter;
    private ObjectSerializer objectSerializer;

    @Before
    public void before() throws IOException {
        socket = mock(Socket.class);
        inputStream = mock(InputStream.class);
        outputStream = mock(OutputStream.class);
        clientConfig = mock(ClientConfig.class);
        requestConverter = mock(RequestConverter.class);
        responseConverter = mock(ResponseConverter.class);
        objectSerializer = mock(ObjectSerializer.class);

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(clientConfig.getRequestConverter()).thenReturn(requestConverter);
        when(clientConfig.getResponseConverter()).thenReturn(responseConverter);
        when(clientConfig.getObjectSerializer()).thenReturn(objectSerializer);

        defaultClient = new DefaultClient(clientConfig) {
            @Override
            protected Socket createSocket(ClientConfig clientConfig) throws IOException {
                return socket;
            }
        };
    }

    @Test
    public void makeRequest() throws IOException {
        Request request = new Request(Command.CLEAR);
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.CLEARED));

        Response response = defaultClient.makeRequest(request);

        assertEquals(Status.CLEARED, response.getStatus());
        verify(requestConverter).writeRequest(outputStream, request);
        verify(responseConverter).readResponse(inputStream);
    }

    @Test
    public void putSimple() throws IOException {
        String key = "key";
        Object value = "value";
        byte[] array = {1, 2, 3};
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.ADDED));
        when(objectSerializer.toByteArray(value)).thenReturn(array);

        Status status = defaultClient.put(key, value);

        assertEquals(Status.ADDED, status);
        verify(objectSerializer).toByteArray(value);
        verify(requestConverter).writeRequest(same(outputStream), equalTo(new Request(Command.PUT, key, null, array)));
    }

    @Test
    public void putFull() throws IOException {
        String key = "key";
        Object value = "value";
        byte[] array = {1, 2, 3};
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.REPLACED));
        when(objectSerializer.toByteArray(value)).thenReturn(array);

        Status status = defaultClient.put(key, value, 1, TimeUnit.MILLISECONDS);

        assertEquals(Status.REPLACED, status);
        verify(objectSerializer).toByteArray(value);
        verify(requestConverter).writeRequest(same(outputStream), equalTo(new Request(Command.PUT, key, 1L, array)));
    }

    @Test
    public void putFullInvalidTtl() throws IOException {
        String key = "key";
        Object value = "value";
        byte[] array = {1, 2, 3};
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.REPLACED));
        when(objectSerializer.toByteArray(value)).thenReturn(array);

        Status status = defaultClient.put(key, value, 1, null);

        assertEquals(Status.REPLACED, status);
        verify(objectSerializer).toByteArray(value);
        verify(requestConverter).writeRequest(same(outputStream), equalTo(new Request(Command.PUT, key, null, array)));
    }

    @Test
    public void get() throws IOException {
        String key = "key";
        Object value = "value";
        byte[] array = {1, 2, 3};
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.GOTTEN, array));
        when(objectSerializer.fromByteArray(array)).thenReturn(value);

        String result = defaultClient.get(key);
        assertEquals(value, result);
        verify(objectSerializer).fromByteArray(array);
        verify(requestConverter).writeRequest(same(outputStream), equalTo(new Request(Command.GET, key)));
    }

    @Test
    public void remove() throws IOException {
        String key = "key";
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.REMOVED));

        Status status = defaultClient.remove(key);
        assertEquals(Status.REMOVED, status);
        verify(requestConverter).writeRequest(same(outputStream), equalTo(new Request(Command.REMOVE, key)));
    }

    @Test
    public void clear() throws IOException {
        when(responseConverter.readResponse(inputStream)).thenReturn(new Response(Status.CLEARED));

        Status status = defaultClient.clear();
        assertEquals(Status.CLEARED, status);
        verify(requestConverter).writeRequest(same(outputStream), equalTo(new Request(Command.CLEAR)));
    }

    @Test
    public void close() throws Exception {
        defaultClient.close();

        verify(socket).close();
    }

    private Request equalTo(final Request request) {
        return argThat(new ArgumentMatcher<Request>() {
            @Override
            public boolean matches(Request arg) {
                return Objects.equals(request.getCommand(), arg.getCommand()) &&
                        Objects.equals(request.getKey(), arg.getKey()) &&
                        Objects.equals(request.getTtl(), arg.getTtl()) &&
                        Objects.equals(request.getData(), arg.getData());
            }
        });
    }

}
