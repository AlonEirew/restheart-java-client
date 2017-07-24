package org.restheartclient.connection;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by Alon Eirew on 7/16/2017.
 */
public class HttpClientBasicFactory implements IHttpClientFactory, Closeable {

    private static final Logger LOGGER = Logger.getLogger(HttpClientBasicFactory.class.getName());

    protected CloseableHttpClient httpClient;

    public HttpClientBasicFactory() {
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        return this.httpClient;
    }

    @Override
    public void close() throws IOException {
        try {
            LOGGER.log(Level.INFO, "Closing HttpClient instance");
            this.httpClient.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception when trying to free http client resource", e);
        }
    }
}
