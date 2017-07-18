package org.restheartclient.connection;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Created by aeirew on 7/16/2017.
 */
public class HttpClientPoolingFactory implements IHttpClientFactory {

    private static final Logger LOGGER = Logger.getLogger(HttpClientPoolingFactory.class.getName());

    private PoolingHttpClientConnectionManager cm;
    private CloseableHttpClient httpClient;

    public HttpClientPoolingFactory() {
        this.cm = new PoolingHttpClientConnectionManager();
        createHttpClient(this.cm);
    }

    public HttpClientPoolingFactory(PoolingHttpClientConnectionManager cm) {
        this.cm = cm;
        createHttpClient(this.cm);
    }

    private void createHttpClient(PoolingHttpClientConnectionManager cm) {
        this.httpClient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        return httpClient;
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
