package org.restheartclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restheartclient.data.RestHeartClientResponse;
import org.restheartclient.utils.GsonUtils;

/**
 * Created by Alon Eirew on 7/12/2017.
 */
public class RestHeartBasicClientApiTest {

    private static final Logger LOGGER = Logger.getLogger(RestHeartBasicClientApiTest.class.getName());

    private static RestHeartClientApi api;

    private String dbName = "testDb";
    private String collName = "testColl";

    private RestHeartClientResponse creationResponseDB;

    @BeforeClass
    public static void initRestHeartClient() {
        api = new RestHeartClientApi();
    }

    @AfterClass
    public static void releaseResources() {
        if (api != null) {
            try {
                api.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to release resources", e);
            }
        }
    }

    @Before
    public void beforeTest() {
        creationResponseDB = createDataBase();
    }

    @After
    public void afterTest() {
        dropDataBase(creationResponseDB);
    }

    @Test
    public void testCreateAndDeleteCollection() {
        RestHeartClientResponse newCollection = createCollection();

        RestHeartClientResponse deleteCollection = api.deleteCollection(dbName, collName, newCollection.getEtag());
        Assert.assertEquals("response as expected", 204, deleteCollection.getStatusCode());
    }

    @Test
    public void testDeleteDocById() throws MalformedURLException {
        createCollection();
        RestHeartClientResponse restHeartClientResponse = insertDocInDB();
        String documentUrlLocation = restHeartClientResponse.getDocumentUrlLocation();
        URL url = new URL(documentUrlLocation);
        String id = FilenameUtils.getName(url.getPath());

        RestHeartClientResponse deleteDocByIdResponse = api.deleteDocumentById(dbName, collName, id);
        Assert.assertEquals(
            "response not as expected, Code" + deleteDocByIdResponse.getStatusCode() + ", ETag-"
                + deleteDocByIdResponse.getEtag(), 204,
            deleteDocByIdResponse.getStatusCode());
    }

    @Test
    public void testGetAllDocs() {
        createCollection();
        insertDocInDB();
        insertDocInDB();

        RestHeartClientResponse response = api.getAllDocumentsFromCollection(dbName, collName);
        Assert.assertNotNull("Response is null", response);

        JsonObject responseObject = response.getResponseObject();
        Assert.assertNotNull("Json object response is null", responseObject);

        JsonElement returned = responseObject.get("_returned");
        int numberOfElements = returned.getAsInt();
        Assert.assertEquals("response size not as expected", 2, numberOfElements);

        LOGGER.info(GsonUtils.toJson(response.getResponseObject()));
    }

    @Test
    public void testGetDocById() throws MalformedURLException {
        createCollection();
        RestHeartClientResponse restHeartClientResponse = insertDocInDB();
        String documentUrlLocation = restHeartClientResponse.getDocumentUrlLocation();
        URL url = new URL(documentUrlLocation);
        String idCreate = FilenameUtils.getName(url.getPath());

        RestHeartClientResponse response = api.getDocumentById(dbName, collName, idCreate);
        Assert.assertNotNull("Response is null", response);

        JsonObject responseObject = response.getResponseObject();
        Assert.assertNotNull("Json object response is null", responseObject);

        String idRes = responseObject.get("_id").getAsJsonObject().get("$oid").getAsString();
        Assert.assertEquals("Id's do not match", idCreate, idRes);

        LOGGER.info(GsonUtils.toJson(response.getResponseObject()));
    }

    @Test
    public void testGetDocQuery() throws MalformedURLException {
        createCollection();
        insertDocInDB();
        insertDocInDB();

        String query = "filter={'name':'John'}";

        RestHeartClientResponse response = api.getDocumentsQuery(dbName, collName, query);
        Assert.assertNotNull("Response is null", response);

        JsonObject responseObject = response.getResponseObject();
        Assert.assertNotNull("Json object response is null", responseObject);

        JsonArray jsonArray = responseObject
            .get("_embedded")
            .getAsJsonArray();

        Assert.assertEquals("Return Collection Name not 2 as expected", 2, jsonArray.size());

        LOGGER.info(GsonUtils.toJson(response.getResponseObject()));
    }

    private RestHeartClientResponse createDataBase() {
        RestHeartClientResponse creationResponse = api.createNewDataBase(dbName, "this is a test");
        Assert.assertEquals("response as expected", 201, creationResponse.getStatusCode());
        Assert.assertNotNull("response eTag is null", creationResponse.getEtag());
        LOGGER.info("ETag=" + creationResponse.getEtag());
        return creationResponse;
    }

    private RestHeartClientResponse createCollection() {
        RestHeartClientResponse newCollection = api.createNewCollection(dbName, collName,
            "this is a test collection");

        Assert.assertEquals("response as expected", 201, newCollection.getStatusCode());
        Assert.assertNotNull("response eTag is null", newCollection.getEtag());
        return newCollection;
    }

    private RestHeartClientResponse insertDocInDB() {
        JsonObject jo = new JsonObject();
        jo.addProperty("Name", "John");
        jo.addProperty("Last", "Smith");

        RestHeartClientResponse restHeartClientResponse = api.insertDocumentInCollection(dbName, collName, jo);

        Assert.assertNotNull("response should not be null", restHeartClientResponse);
        Assert.assertNotNull("response headers should exist", restHeartClientResponse.getHeaders());
        Assert.assertTrue("Headers not empty", restHeartClientResponse.getHeaders().length > 0);
        Assert.assertNotNull("etag header missing", restHeartClientResponse.getEtag());

        return restHeartClientResponse;
    }

    private void dropDataBase(RestHeartClientResponse creationResponseSameName) {
        RestHeartClientResponse deleteResponse = api.deleteDataBase(dbName, creationResponseSameName.getEtag());

        Assert.assertEquals(
            "response not as expected, Code" + deleteResponse.getStatusCode() + " ETag-"
                + deleteResponse.getEtag(), 204,
            deleteResponse.getStatusCode());
        LOGGER.info("ETag=" + deleteResponse.getEtag());
    }
}
