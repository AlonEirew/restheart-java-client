package org.restheartclient;

import com.google.gson.JsonObject;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.restheartclient.data.RestHeartClientRequest;
import org.restheartclient.data.RestHeartClientRequestBuilder;
import org.restheartclient.data.RestHeartClientResponse;

/**
 * Created by aeirew on 7/12/2017.
 */
public class RestHeartClientApiTest {

    private static final Logger LOGGER = Logger.getLogger(RestHeartClientApiTest.class.getName());

    private RestHeartClientApi api = new RestHeartClientApi();

    @Test
    public void testCreateAndDeleteDB() {
        /* TEST CREATION OF DB */
        String dbName = "test";
        createDataBase(dbName);

        RestHeartClientRequestBuilder requestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest request = requestBuilder.setDataBaseName(dbName).setDescription("this is a test")
            .build();
        RestHeartClientResponse creationResponseSameName = api.createNewDataBase(request);
        Assert.assertEquals("response as expected", 200, creationResponseSameName.getStatusCode());
        Assert.assertNotNull("response eTag is null", creationResponseSameName.getEtag());
        LOGGER.info("ETag=" + creationResponseSameName.getEtag());
        /**/

        /* TEST DELETE DB */
        dropDataBase("test", creationResponseSameName);
        /**/
    }

    @Test
    public void testCreateAndDeleteCollection() {
        /* CREATE COLLECTION AND DATABASE */
        String dbName = "testDb";
        String collName = "testColl";
        RestHeartClientResponse creationResponseDB = createDataBase(dbName);
        RestHeartClientResponse newCollection = createCollection(dbName, collName);
        /**/

        /* DELETE COLLECTION */
        RestHeartClientRequestBuilder requestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest request = requestBuilder.setDataBaseName(dbName).setCollectionName(collName)
            .setETag(newCollection.getEtag()).setDescription("this is a test").build();
        RestHeartClientResponse deleteCollection = api.deleteCollection(request);
        Assert.assertEquals("response as expected", 204, deleteCollection.getStatusCode());
        /**/

        dropDataBase(dbName, creationResponseDB);
    }

    private RestHeartClientResponse createCollection(String dbName, String collName) {
        RestHeartClientRequestBuilder requestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest request = requestBuilder.setDataBaseName(dbName).setCollectionName(collName)
            .setDescription("this is a test collection").build();
        RestHeartClientResponse newCollection = api.createNewCollection(request);

        Assert.assertEquals("response as expected", 201, newCollection.getStatusCode());
        Assert.assertNotNull("response eTag is null", newCollection.getEtag());
        return newCollection;
    }

    @Test
    public void testInsertAndDeleteDocument() {
        /* CREATE, GET AND DELETE DOCUMENT IN COLLECTION */
        String dbName = "testDb";
        String collName = "testColl";
        RestHeartClientResponse creationResponseDB = createDataBase(dbName);
        RestHeartClientResponse newCollection = createCollection(dbName, collName);
        JsonObject jo = new JsonObject();
        jo.addProperty("Name", "John");
        jo.addProperty("Last", "Smith");

        RestHeartClientRequestBuilder insertRequestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest insertRequest = insertRequestBuilder.setDataBaseName(dbName).setCollectionName(collName)
            .setRequestObject(jo).build();

        RestHeartClientRequestBuilder deleteRequestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest deleteRequest = deleteRequestBuilder.setDataBaseName(dbName).setCollectionName(collName)
            .build();

        RestHeartClientResponse insertResponse = api.insertDocumentInCollection(insertRequest);

        RestHeartClientResponse deleteResponse = api.deleteDocumentById(deleteRequest);
        /**/

        dropDataBase(dbName, creationResponseDB);
    }

    private RestHeartClientResponse createDataBase(String dbName) {
        RestHeartClientRequestBuilder requestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest request = requestBuilder.setDataBaseName(dbName).setDescription("this is a test")
            .build();
        RestHeartClientResponse creationResponse = api.createNewDataBase(request);
        Assert.assertEquals("response as expected", 201, creationResponse.getStatusCode());
        Assert.assertNotNull("response eTag is null", creationResponse.getEtag());
        LOGGER.info("ETag=" + creationResponse.getEtag());
        return creationResponse;
    }

    private void dropDataBase(String dbName, RestHeartClientResponse creationResponseSameName) {
        RestHeartClientRequestBuilder requestBuilder = new RestHeartClientRequestBuilder();
        RestHeartClientRequest request = requestBuilder.setDataBaseName(dbName).setETag(creationResponseSameName.getEtag())
            .build();

        RestHeartClientResponse deleteResponse = api.deleteDataBase(request);
        Assert.assertEquals(
            "response not as expected, Code" + deleteResponse.getStatusCode() + " ETag-"
                + deleteResponse.getEtag(), 204,
            deleteResponse.getStatusCode());
        LOGGER.info("ETag=" + deleteResponse.getEtag());
    }
}
