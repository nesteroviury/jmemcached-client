package ru.dev.jmemcached.client;

import ru.dev.jmemcached.client.impl.JMemcachedClientFactory;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class ClientExample {

    public static void main(String[] args) throws Exception {
        try(Client client = JMemcachedClientFactory.buildNewClient()){
            String key = "test";
            String testData = "Hello world";
            client.put(key, testData);
            System.out.println(client.get(key));

            client.remove(key);
            System.out.println(client.get(key));

            client.put(key, testData);
            client.put(key, new BussinessObject("TEST"));
            System.out.println(client.get(key));

            client.clear();
            System.out.println(client.get(key));

            client.put(key, testData, 2, TimeUnit.SECONDS);
            TimeUnit.SECONDS.sleep(3);
            System.out.println(client.get(key));
        }
    }

    private static class BussinessObject implements Serializable{

        private String name;

        BussinessObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "BussinessObject{" +
                    "name='" + name + '\'' +
                    '}';
        }

    }

}
