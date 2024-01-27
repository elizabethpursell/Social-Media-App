package edu.lehigh.cse216.emp520.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import spark.Spark;
import com.google.gson.*;

import java.util.Map;
import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 4567;

    /**
     * Create the test case; Set port for all tests
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    /**
     * Test the database connection
     */
    public void testConnection() {
        Database db = getDatabaseConnection();
        assertTrue( db != null );
    }

    /**
     * Test the database disconnect
     */
    public void testDisconnect() {
        Database db = getDatabaseConnection();
        boolean disconnected = db.disconnect();
        assertTrue(disconnected);
    }

    /**
     * Test GET /key/users
     */
    public void testGetUsers() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.get("/1234567890/users", (request, response) -> {
            response.status(200);
            response.type("application/json");
            StructuredResponse r = new StructuredResponse("ok", null, db.selectAllUsers());
            assertTrue(r.mStatus != "invalid");
            return gson.toJson(r);
        });
        db.disconnect();
    }

    /**
     * Test GET /key/messages
     */
    public void testGetMessages() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.get("/1234567890/messages", (request, response) -> {
            response.status(200);
            response.type("application/json");
            StructuredResponse r = new StructuredResponse("ok", null, db.selectAllIdeas());
            assertTrue(r.mStatus != "invalid");
            return gson.toJson(r);
        });
        db.disconnect();
    }

    /**
     * Test GET /key/messages/id
     */
    public void testGetMessageByID() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.get("/1234567890/messages/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            response.status(200);
            response.type("application/json");
            DataRowIdea data = db.selectIdea(idx);
            if (data == null) {
                StructuredResponse r = new StructuredResponse("error", idx + " not found", null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", null, data);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

    /**
     * Test GET /key/users/username/comments
     */
    public void testGetCommentsByUser() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.get("/1234567890/users/:username/comments", (request, response) -> {
            String name = request.params("username");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            ArrayList<DataRowComment> data = db.selectCommentsByUser(name);
            if (data == null) {
                StructuredResponse r = new StructuredResponse("error", name + " not found", null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", null, data);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

    /**
     * Test POST /key/messages
     
    public void testNewPost() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.post("/1234567890/messages", (request, response) -> {
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            int newId = db.insertIdea(req.uUsername, req.mContent);      // default to 0 likes
            if (newId == -1) {
                StructuredResponse r = new StructuredResponse("error", "error performing insertion", null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", "" + newId, null);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

    /**
     * Test POST /key/comments
     
    public void testNewComment() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.post("/1234567890/comments", (request, response) -> {
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            int newId = db.insertComment(req.uUsername, req.mId, req.cContent);     // default to 0 likes
            if (newId == -1) {
                StructuredResponse r = new StructuredResponse("error", "error performing insertion", null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", "" + newId, null);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

*/
    /**
     * Test post key/messages/username/id
     */
    public void testUpdateVotes() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.post("/1234567890/messages/:username/:id", (request, response) -> {
            String name = request.params("username");
            int id = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            int result = -1;
            if(db.findVote(name, id))
            {
                result = db.updateVote(name, id, req.vote);
            }
            else
            {
                result = db.insertVote(name, id, req.vote);
            }
            if (result == -1) {
                StructuredResponse r = new StructuredResponse("error", "unable to update row " + id, null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", null, result);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

    /**
     * Test put key/users/username
     */
    public void testUpdateUser() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.post("/1234567890/users/:username", (request, response) -> {
            String name = request.params("username");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            int result = db.updateUser(name, req.uGI, req.uSO, req.uNote);
            if (result == -1) {
                StructuredResponse r = new StructuredResponse("error", "unable to update row " + name, null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", null, result);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

    /**
     * Test put key/comments/id
     */
    public void testUpdateComment() {
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();
        Spark.post("/1234567890/comments/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int result = db.updateComment(idx, req.mContent);
            if (result == -1) {
                StructuredResponse r = new StructuredResponse("error", "unable to update row " + idx, null);
                assertTrue(r.mStatus == "error");
                return gson.toJson(r);
            } else {
                StructuredResponse r = new StructuredResponse("ok", null, result);
                assertTrue(r.mStatus == "ok" && r.mData != null);
                return gson.toJson(r);
            }
        });
        db.disconnect();
    }

    /**
     * Get a fully-configured connection to the database, or exit immediately
     * Uses the Postgres configuration from environment variables
     * 
     * Copy of function from App.java
     * 
     * @return Database (null on failure)
     */
    private static Database getDatabaseConnection(){
        if( System.getenv("DATABASE_URL") != null ){
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB);
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, "", user, pass);
    }

    /**
     * Get an integer environment variable if it exists, and otherwise return the
     * default value.
     * 
     * Copy of function from App.java
     * 
     * @param envar name of the environment variable to get
     * @param defaultVal default value if environment variable not given
     * @return int (given environment variable as int or default value)
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }
}
