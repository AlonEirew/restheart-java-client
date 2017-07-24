package org.restheartclient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.restheartclient.connection.HttpConnectionUtils;
import org.restheartclient.data.RestHeartClientResponse;
import org.restheartclient.utils.GsonUtils;
import org.restheartclient.utils.MongoURLBuilder;

/**
 * Created by Alon Eirew on 7/12/2017.
 */
public class RestHeartClientApi implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(RestHeartClientApi.class.getName());

    /* RestHeart Mongo Db Constants Declaration */
    private static final String ETAG_CONDITION = "If-Match";
    private static final String CREATED_ON_TAG = "created_on";
    private static final String CURRENT_DATE_TAG = "$currentDate";
    private static final String DESCRIPTION_TAG = "description";
    private static final String DEFAULT_LOCALHOST_URL = "http://127.0.0.1:8080/";
    /**/

    private String mongoUrl = DEFAULT_LOCALHOST_URL;
    private HttpConnectionUtils httpConnectionUtils;

    public RestHeartClientApi() {
        this.httpConnectionUtils = new HttpConnectionUtils();
    }

    public RestHeartClientApi(String mongoUrl) {
        this.mongoUrl = mongoUrl;
    }

    public RestHeartClientApi(HttpConnectionUtils httpConnectionUtils) {
        this.httpConnectionUtils = httpConnectionUtils;
    }

    public RestHeartClientApi(String mongoUrl, HttpConnectionUtils httpConnectionUtils) {
        this.mongoUrl = mongoUrl;
        this.httpConnectionUtils = httpConnectionUtils;
    }

    /**
     * @param databaseName Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse createNewDataBase(final String databaseName) {
        return createNewDataBase(databaseName, null);
    }

    /**
     * @param databaseName Required
     * @param databaseDescription Optional
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse createNewDataBase(final String databaseName, final String databaseDescription) {
        RestHeartClientResponse response = null;
        LOGGER.info("Trying to create new db-" + databaseName + " with desc-" + databaseDescription);

        JsonObject jo = new JsonObject();
        JsonObject currentDate = new JsonObject();
        currentDate.addProperty(CREATED_ON_TAG, true);
        jo.add(CURRENT_DATE_TAG, currentDate);

        if (databaseDescription != null) {
            jo.addProperty(DESCRIPTION_TAG, databaseDescription);
        }

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .build();

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, jo)) {
            response = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to create new Mongo DB with name-" + databaseName
                + " and description" + databaseDescription, e);
        }

        return response;
    }

    /**
     * @param databaseName Required
     * @param databaseETag Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse deleteDataBase(final String databaseName, final String databaseETag) {
        RestHeartClientResponse response = null;
        LOGGER.info("Trying to create new db-" + databaseName);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .build();

        List<Header> headers = createHeadersList(databaseETag);
        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, headers)) {
            response = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "was unable to delete Mongo DB with name-" + databaseName, e);
        }

        return response;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse createNewCollection(final String databaseName, final String collectionName) {
        return createNewCollection(databaseName, collectionName, null);
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @param collectionDescription Optional
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse createNewCollection(final String databaseName, final String collectionName,
        final String collectionDescription) {
        RestHeartClientResponse response = null;
        LOGGER.info("Trying to create new collection-" + collectionName + " in DB-" +
            databaseName + " with desc-" + collectionDescription);

        JsonObject jo = new JsonObject();
        if (collectionDescription != null) {
            jo.addProperty(DESCRIPTION_TAG, collectionDescription);
        }

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .build();

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, jo)) {
            response = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "was unable to create new Mongo DB collection with name-" + collectionName
                + "in DB-" + databaseName, e);
        }

        return response;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @param collectionETag Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse deleteCollection(final String databaseName, final String collectionName,
        final String collectionETag) {
        RestHeartClientResponse response = null;
        LOGGER.info("Trying to create new db-" + databaseName);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .build();
        List<Header> headers = createHeadersList(collectionETag);

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, headers)) {
            response = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "was unable to delete Mongo DB collection from DB name-" +
                databaseName + ", collection name-" + collectionName + " and collection etag-" + collectionETag, e);
        }

        return response;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @param documentToInsert Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse insertDocumentInCollection(final String databaseName, final String collectionName,
        final Object documentToInsert) {
        RestHeartClientResponse response = null;
        LOGGER.info("Trying to insert document in collection-" + collectionName + " and DB-" + databaseName);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .build();

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPost(url, documentToInsert)) {
            response = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to insert document-"
                + GsonUtils.toJson(documentToInsert) + " to DB with name-" + databaseName
                + " and collection-" + collectionName, e);
        }

        return response;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @param documentId Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse deleteDocumentById(final String databaseName, final String collectionName,
        final String documentId) {
        RestHeartClientResponse response = null;
        LOGGER.info("Trying to create new db-" + databaseName);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .setDocumentId(documentId).build();

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, null)) {
            response = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to delete document from DB name-" + databaseName +
                ", collection name-" + collectionName + " and document id-" + documentId, e);
        }

        return response;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse getAllDocumentsFromCollection(final String databaseName,
        final String collectionName) {
        RestHeartClientResponse restHeartClientResponse = null;
        LOGGER.info("Trying to get all documents from db-" + databaseName + ", collection-" + collectionName);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .build();

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpGet(url)) {
            restHeartClientResponse = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                "Was unable to get Mongo DB collection documents with DB name-" + databaseName +
                    " and collection name-" + collectionName, e);
        }

        return restHeartClientResponse;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @param documentId Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse getDocumentById(final String databaseName,
        final String collectionName, final String documentId) {
        RestHeartClientResponse restHeartClientResponse = null;
        LOGGER.info("Trying to get document by id from db-" + databaseName +
            ", collection-" + collectionName + " and documentId-" + documentId);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String url = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .setDocumentId(documentId).build();

        try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpGet(url)) {
            restHeartClientResponse = extractFromResponse(httpResponse);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to delete document from Mongo DB with name-" +
                databaseName + ", collection name-" + collectionName + " and document id-" + documentId, e);
        }

        return restHeartClientResponse;
    }

    /**
     * @param databaseName Required
     * @param collectionName Required
     * @param query Required
     * @return {@link RestHeartClientResponse}
     */
    public RestHeartClientResponse getDocumentsQuery(String databaseName,
        String collectionName, String query) {
        RestHeartClientResponse restHeartClientResponse = null;
        LOGGER.info("Trying to get document with query from db-" + databaseName +
            ", collection-" + collectionName + " and query-" + query);

        MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
        String urlStr = mongoURLBuilder
            .setBaseURL(this.mongoUrl)
            .setDatabaseName(databaseName)
            .setCollectionName(collectionName)
            .build();

        CloseableHttpResponse httpResponse = null;
        try {
            String encode = URLEncoder.encode(query, "UTF-8");
            URL url = new URL(urlStr + "?" + encode);
            httpResponse = this.httpConnectionUtils.sendHttpGet(url.toString());
            restHeartClientResponse = extractFromResponse(httpResponse);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Was unable to delete, request URL malformed "
                + "document from Mongo DB with name-" + databaseName + ", collection name-" +
                collectionName + " and query-" + query, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to delete document from Mongo DB with name-" +
                databaseName + ", collection name-" + collectionName + " and query-" + query, e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Was unable to close the response, "
                        + "possible memory leak", e);
                }
            }
        }

        return restHeartClientResponse;
    }

    private RestHeartClientResponse extractFromResponse(final CloseableHttpResponse httpResponse) {
        RestHeartClientResponse response = null;
        JsonObject responseObj = null;
        if (httpResponse != null) {
            StatusLine statusLine = httpResponse.getStatusLine();
            Header[] allHeaders = httpResponse.getAllHeaders();
            HttpEntity resEntity = httpResponse.getEntity();
            if (resEntity != null) {
                try {
                    String responseStr = IOUtils.toString(resEntity.getContent(), "UTF-8");
                    if (responseStr != null && !responseStr.isEmpty()) {
                        JsonParser parser = new JsonParser();
                        responseObj = parser.parse(responseStr).getAsJsonObject();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Was unable to extract response body", e);
                }
            }

            response = new RestHeartClientResponse(statusLine, allHeaders, responseObj);
        }
        return response;
    }

    private List<Header> createHeadersList(final String collETag) {
        List<Header> headers = new ArrayList<>();
        Header header = new BasicHeader(ETAG_CONDITION, collETag);
        headers.add(header);
        return headers;
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("Releasing all RestHeart-Client resources");
        this.httpConnectionUtils.close();
    }
}
