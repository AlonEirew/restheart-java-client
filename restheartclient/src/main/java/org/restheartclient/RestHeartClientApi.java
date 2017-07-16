package org.restheartclient;

import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.IOException;
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
import org.restheartclient.data.RestHeartClientRequest;
import org.restheartclient.data.RestHeartClientResponse;
import org.restheartclient.utils.GsonUtils;

/**
 * Created by aeirew on 7/12/2017.
 */
public class RestHeartClientApi implements Closeable {

    /* RestHeart MongoDb Constants Declaration */
    private static final String ETAG_CONDITION = "If-Match";
    private static final String CREATED_ON_TAG = "created_on";
    private static final String CURRENT_DATE_TAG = "$currentDate";
    private static final String DESCRIPTION_TAG = "description";

    private static String DEFAULT_MONGO_URL = "http://127.0.0.1:8080/";

    private static final Logger LOGGER = Logger.getLogger(RestHeartClientApi.class.getName());

    private String mongoUrl = DEFAULT_MONGO_URL;

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

    public RestHeartClientResponse createNewDataBase(final RestHeartClientRequest request) {
        RestHeartClientResponse response = null;
        if (request != null) {
            String dbName = request.getDataBaseName();
            String dbDesc = request.getDescription();
            LOGGER.info("Trying to create new db-" + dbName + " with desc-" + dbDesc);
            if (dbName != null && !dbName.isEmpty()) {
                JsonObject jo = new JsonObject();
                JsonObject currentDate = new JsonObject();
                currentDate.addProperty(CREATED_ON_TAG, true);
                jo.add(CURRENT_DATE_TAG, currentDate);

                if (dbDesc != null) {
                    jo.addProperty(DESCRIPTION_TAG, dbDesc);
                }
                String url = this.mongoUrl + dbName;
                try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, jo)) {
                    response = extractFromResponse(httpResponse);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Was unable to create new Mongo DB with name-" + dbName
                        + " and description" + dbDesc, e);
                }
            }
        }

        return response;
    }

    public RestHeartClientResponse deleteDataBase(final RestHeartClientRequest request) {
        RestHeartClientResponse response = null;
        if (request != null) {
            String dbName = request.getDataBaseName();
            String dbETag = request.getETag();
            LOGGER.info("Trying to create new db-" + dbName);
            if (dbName != null && !dbName.isEmpty() && dbETag != null && !dbETag.isEmpty()) {
                String url = this.mongoUrl + dbName;
                List<Header> headers = createHeadersList(dbETag);
                try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, headers)) {
                    response = extractFromResponse(httpResponse);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "was unable to delete Mongo DB with name-" + dbName, e);
                }
            }
        }

        return response;
    }

    public RestHeartClientResponse createNewCollection(final RestHeartClientRequest request) {
        RestHeartClientResponse response = null;
        if (request != null) {
            String dbName = request.getDataBaseName();
            String coll = request.getCollectionName();
            String desc = request.getDescription();
            LOGGER.info(
                "Trying to create new collection-" + coll + " in DB-" + dbName + " with desc-" + desc);
            if (dbName != null && !dbName.isEmpty() && coll != null && !coll.isEmpty()) {
                JsonObject jo = new JsonObject();
                if (desc != null) {
                    jo.addProperty(DESCRIPTION_TAG, desc);
                }
                String url = this.mongoUrl + dbName + "/" + coll;
                try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, jo)) {
                    response = extractFromResponse(httpResponse);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "was unable to create new Mongo DB collection with name-" + coll
                        + "in DB-" + dbName, e);
                }
            }
        }

        return response;
    }

    public RestHeartClientResponse deleteCollection(final RestHeartClientRequest request) {
        RestHeartClientResponse response = null;
        if (request != null) {
            String dbName = request.getDataBaseName();
            String collName = request.getCollectionName();
            String collETag = request.getETag();
            LOGGER.info("Trying to create new db-" + dbName);
            if (dbName != null && !dbName.isEmpty() && collName != null && !collName.isEmpty()
                && collETag != null && !collETag.isEmpty()) {
                String url = this.mongoUrl + dbName + "/" + collName;
                List<Header> headers = createHeadersList(collETag);

                try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, headers)) {
                    response = extractFromResponse(httpResponse);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "was unable to delete Mongo DB with name-" + dbName, e);
                }
            }
        }

        return response;
    }

    public RestHeartClientResponse insertDocumentInCollection(final RestHeartClientRequest request) {
        RestHeartClientResponse response = null;
        if (request != null) {
            String dbName = request.getDataBaseName();
            String collName = request.getCollectionName();
            Object document = request.getRequestObject();
            LOGGER
                .info("Trying to insert document in collection-" + collName + " and DB-" + dbName);

            if (dbName != null && !dbName.isEmpty() && collName != null && !collName.isEmpty()
                && document != null) {
                String url = this.mongoUrl + dbName + "/" + collName;
                try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPost(url, document)) {
                    response = extractFromResponse(httpResponse);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Was unable to insert document-"
                        + GsonUtils.toJson(document) + " to DB with name-" + dbName
                        + " and collection-" + collName, e);
                }
            }
        }

        return response;
    }

    public RestHeartClientResponse deleteDocumentById(final RestHeartClientRequest request) {
        RestHeartClientResponse response = null;
        if (request != null) {
            String dbName = request.getDataBaseName();
            String collName = request.getCollectionName();
            String docId = request.getId();
            LOGGER.info("Trying to create new db-" + dbName);
            if (dbName != null && !dbName.isEmpty() && collName != null && !collName.isEmpty()
                && docId != null && !docId.isEmpty()) {
                String url = this.mongoUrl + dbName + "/" + collName + "/" + docId;
                try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, null)) {
                    response = extractFromResponse(httpResponse);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Was unable to delete Mongo DB with name-" + dbName, e);
                }
            }
        }

        return response;
    }

    public <T> T getAllDocuments(final RestHeartClientRequest request) {
        if (request != null) {
            String dbName = request.getDataBaseName();
            LOGGER.info("Trying to get all docs from db-" + dbName);
            RestHeartClientResponse response = null;
        }

        return null;
    }

    public <T> T getDocumentById() {
        return null;
    }

    public <T> T getDocumentQuery() {
        return null;
    }

    private RestHeartClientResponse extractFromResponse(final CloseableHttpResponse httpResponse) {
        RestHeartClientResponse response = null;
        if (httpResponse != null) {
            StatusLine statusLine = httpResponse.getStatusLine();
            Header[] allHeaders = httpResponse.getAllHeaders();
            HttpEntity resEntity = httpResponse.getEntity();
            String responseStr = null;
            if (resEntity != null) {
                try {
                    responseStr = IOUtils.toString(resEntity.getContent(), "UTF-8");
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Was unable to extract response body", e);
                }
            }

            response = new RestHeartClientResponse(statusLine, allHeaders, responseStr);
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
        this.httpConnectionUtils.close();
        this.httpConnectionUtils = null;
    }
}
