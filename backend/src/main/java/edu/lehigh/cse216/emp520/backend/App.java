package edu.lehigh.cse216.emp520.backend;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Arrays;
import java.util.Base64;
//import java.util.Collection;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
// import Google's JSON library
import com.google.gson.Gson;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayOutputStream;
//import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileWriter;
import java.io.IOException;
//import java.util.Arrays;

// import the Spark package
import spark.Spark;

/**
 * App creates an HTTP server to get, put, post, delete data
 */
public class App {
    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 4567;
    
    /** 
     * Main method to run CRUD commands
     * 
     * @param args array of command line arguments
     */
    public static void main(String[] args) throws IOException {
       
        final Gson gson = new Gson();
        // connect to database
        Database db = getDatabaseConnection();
        final Hashtable<Integer, String> HT = new Hashtable<>();
        HT.put(1234567890, "admin");
        // set the port on which to listen for requests from the environment
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));

        // Set up the location for serving static files.  If the STATIC_LOCATION
        // environment variable is set, we will serve from it.  Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        if ("True".equalsIgnoreCase(System.getenv("CORS_ENABLED"))) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
        // Specify the CLIENT_ID of the app that accesses the backend:
        //.setAudience(Collections.singletonList("2426363556-a9c89gtd6h8lnt7d0vq4rcm5o24pusfk.apps.googleusercontent.com"))
        // Or, if multiple clients access the backend:
        .setAudience(Arrays.asList("2426363556-a9c89gtd6h8lnt7d0vq4rcm5o24pusfk.apps.googleusercontent.com",
        "1062513849505-op7q17t0b43ef7n581nbjopa0m6t4q6c.apps.googleusercontent.com", "1062513849505-imm66c35rrd6pecsrv0uqr8su88nengf.apps.googleusercontent.com"))
        .build();

        String CREDS = "{\n"
            + "    \"type\": \"service_account\",\n"
            + "    \"project_id\": \"fiery-glass-405720\",\n"
            + "    \"private_key_id\": \"b4ceb8258153dad9f0187c016b9b10302dcd7274\",\n"
            + "    \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCxA29PAIBCfdAt\\nbggw+4SJ1v5q5K0k0uHDJyIthVuNkAEi5Ry6OEGpdpVPoScD3ZYTQ7yJZBW8IYe2\\nwaZptJnvbkIS7xQQ6MUdJnu7DNqGzKx6TrTF5Gfl9OtYKP21kL3HD+vr4/x9Dm6Z\\n6ZYnZ6ECHL/Cs6tnSWW0uojiJvDVpoxW6Mxwoc9tpYbgaSK3wwVPEcjbt5ITI5OO\\nGOhi2Rp1OV3PixymOkzTeABe9qrvBw9pomPFoptcIvX7JEKIJltj/dDxsDR20M78\\n0lHqBXLwHDYtqvEkx9jdVSf6NmKQ9ieFO6guyVekyr5IzzzuJO4RmDW1WzVfFa4p\\nLV1y0KnPAgMBAAECggEAUKZLexez2FehV2mIEuUtzG2nqkU5PAtW8EbutAXMIlIH\\nqoHv1kqWZCMSwV6fu4ukem+EwaZXnsk6H8LYUtYFwsieEylZn6CmWnMzsZQYJo2v\\nUeR1SaqYyfWOmrLGcYiaZs1yxJ2x5rqCGt8J2jdFbQRzPD8HK76Nt8u7XSq31OdU\\nc6GVvuQVguU+WAUN+0pNuZEuTco27YKUCVUUDoPktOLP1AW/aK/ZJTpva6514qaq\\nf1/p1UKWp1YJ9fP6zTRxu++xaxRZFo85omLuq1D7n4YTByA+5h1aai3YyTaWm/ST\\nCoFAMr6qPpkUkx4LCMbTOPv37o2HV6pK5zidLWqZsQKBgQDcsabIeBW551Kgjzol\\na/s8qS1KP1xswt4r502QJbfDx9s09Gd0+XIfSmcKnJs2subCfFwz0BImKTPbmOS+\\n5cN3S5MbTsS5cMIPFJulfdtIIZ6GLNEy9SKtVPZTotHGIgbQDdWnO3QjWs4zr2At\\n7thaGXuDPmHe7MSFk/LiueSg/wKBgQDNVOGRswsdLT0kdJsUY9kX8OYEeR+GHE6i\\nQ9NGrwvrjJL2PDu9DbCs00cUL317TGgi7v5xUDz3NTv8z0KvJ/I8ZQ0zIxUlcaTD\\nNhGeUkADUkSeHyE4SEZQEGUl/qd7QxwnwjDoCB7TtwrwaGWSGsP31JuPSZysC/7f\\nuJXZD3knMQKBgFZh4tpMxpRBiwH1jdjf7zLLNUL+kfqwO64Llzx4xvkG3TJZB7Wt\\nfZHp2XM54TcDx3cQnjZZlwEA1594tPBTlrK3Dhl+N1ouXIbylgmsvYv6PMZf/HJp\\nqO0XeGM8M8fNwcTl5V7T0p2UCWoJlyfjeSOrHcE9RamwGyv9wPAluuuZAoGBAMXM\\nuAOgY9t2ggkFX4NT8IU0ppC2kdyilkmQZw4XgLcn00brnWywrHrAiR9z5ECLyWGl\\ns207K4/FM4WFr6qGI790ZTPW3v8UK/F1u6E1gL7yWHGucVUDouBr+tSQLYz7iDrw\\nwiYz9GZlVV9kYjxOFmw+3qA4/HlskNm5unjAhChxAoGAY1y3+QnkOi+2mCH1qquL\\neRNFywap/eX2pwhTr4mOj3fb94ZWI14As0EKaQE5jn71E4OPpaUqZ7zO/qgnLEWf\\nJEm98jdZmL8M82xrCmtSgZbCqI7tSdW2nk1BbQzPrrSiNBCtxe9NFpZLjxn5R78X\\nyVpZiQ0zJPm+5yE8lUc6wMA=\\n-----END PRIVATE KEY-----\\n\",\n"
            + "    \"client_email\": \"team-snems-file-storage@fiery-glass-405720.iam.gserviceaccount.com\",\n"
            + "    \"client_id\": \"111794579203975391978\",\n"
            + "    \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
            + "    \"token_uri\": \"https://oauth2.googleapis.com/token\",\n"
            + "    \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
            + "    \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/team-snems-file-storage%40fiery-glass-405720.iam.gserviceaccount.com\",\n"
            + "    \"universe_domain\": \"googleapis.com\"\n"
            + "  }";
        java.io.File credFile = new java.io.File("creds");
        try{
            java.io.FileWriter writer = new java.io.FileWriter(credFile);
            writer.write(CREDS);
            writer.close();
        }
        catch (IOException e){
            
        }
        System.out.println(CREDS);
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credFile)).createScoped(Collections.singleton(DriveScopes.DRIVE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
    
        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer)
            .setApplicationName("Drive samples")
            .build();
        
        /** 
         * GET route for setting up the frontend
         */
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        /** 
         * GET route for getting all the users
         * :key is an entered session key
         * 
         * returns all the users in the database
         */
        Spark.get("/:key/users", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                return gson.toJson(new StructuredResponse("ok", null, db.selectAllUsers()));
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * GET route for getting a specific user
         * key is an entered session key
         * username is the username of the user
         * 
         * returns the data of the requested user
         */
        Spark.get("/:key/users/:username", (request, response) -> {
            String name = request.params("username");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                DataRowUser data = db.selectUser(name);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", name + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * GET route for getting all the comments made by a user
         * key is an entered session key
         * username is the username of the user
         * 
         * returns all comments made by the given user
         */
        Spark.get("/:key/users/:username/comments", (request, response) -> {
            String name = request.params("username");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                ArrayList<DataRowComment> data = db.selectCommentsByUser(name);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", name + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });


        /** 
         * GET route for getting all the votes made by a user
         * key is an entered session key
         * username is the username of the user
         * 
         * returns all cotes made by the given user
         */
        Spark.get("/:key/users/:username/votes", (request, response) -> {
            String name = request.params("username");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                ArrayList<DataRowVote> data = db.selectVotesByUser(name);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", name + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * GET route for getting all the messages
         * key is an entered session key
         * 
         * returns all the messages
         */
        Spark.get("/:key/messages", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                ArrayList<DataRowIdea> ideas = db.selectAllIdeas();
                for (DataRowIdea rd : ideas) {
                    File theFile = service.files().get(rd.mBase64).execute();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1000000);
                    service.files().get(rd.mBase64).executeMediaAndDownloadTo(outputStream);
                    byte[] l = outputStream.toByteArray();
                    String content = new String(l);
                    rd.mBase64 = content;
                    rd.mFilename = theFile.getName();
                    ArrayList<DataRowComment> comments = db.selectCommentsByIdea(rd.mId);
                    for (DataRowComment rd2 : comments){
                        File theCommentFile = service.files().get(rd2.cBase64).execute();
                        ByteArrayOutputStream commentOutputStream = new ByteArrayOutputStream(1000000);
                        service.files().get(rd2.cBase64).executeMediaAndDownloadTo(commentOutputStream);
                        byte[] l2 = commentOutputStream.toByteArray();
                        String commentContent = new String(l2);
                        rd2.cBase64 = commentContent;
                        rd2.cFilename = theCommentFile.getName();
                    }
                    rd.mComments = comments;
                }
                return gson.toJson(new StructuredResponse("ok", null, ideas));
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });


        /** 
         * GET route for getting a specific idea
         * key is an entered session key
         * id is the idea_id of the idea
         * 
         * returns the data of the requested idea, including its comments
         */
        Spark.get("/:key/messages/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                DataRowIdea data = db.selectIdea(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    File theFile = service.files().get(data.mBase64).execute();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1000000);
                    service.files().get(data.mBase64).executeMediaAndDownloadTo(outputStream);
                    byte[] l = outputStream.toByteArray();
                    String content = new String(l);
                    data.mBase64 = content;
                    data.mFilename = theFile.getName();
                    ArrayList<DataRowComment> comments = db.selectCommentsByIdea(data.mId);
                    for (DataRowComment rd2 : comments){
                        File theCommentFile = service.files().get(rd2.cBase64).execute();
                        ByteArrayOutputStream commentOutputStream = new ByteArrayOutputStream(1000000);
                        service.files().get(rd2.cBase64).executeMediaAndDownloadTo(commentOutputStream);
                        byte[] l2 = commentOutputStream.toByteArray();
                        String commentContent = new String(l2);
                        rd2.cBase64 = commentContent;
                        rd2.cFilename = theCommentFile.getName();
                    }
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            } else {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * GET route for getting all the comments made under an idea
         * key is an entered session key
         * id is the idea_id of the idea
         * 
         * returns all comments made under the idea
         */
        Spark.get("/:key/messages/:id/comments", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                ArrayList<DataRowComment> data = db.selectCommentsByIdea(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * POST route for creating a user
         * 
         * idtoken is the google oauth idtoken sent by the frontend
         * 
         * verifies the idtoken with google to get a payload, creates a new user if the user
         * does not exist already, then hashes the user email, storing this hash in the hashtable
         * and returning the sessionkey to the frontend
         */
        Spark.post("/users", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            // NB: createEntry checks for null title and message
            GoogleIdToken idToken = verifier.verify(req.idtoken);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String[] emailSlice = email.split("@");
                if(!db.findUser(emailSlice[0]) || db.checkValidUser(emailSlice[0]))
                {
                    if(emailSlice[1].equals("lehigh.edu"))
                    {
                        if(!(db.findUser(emailSlice[0])))
                        {
                            db.insertUser(emailSlice[0], email, "", "", ""); 
                        }
                        int sessionKey = email.hashCode();
                        HT.put(sessionKey, emailSlice[0]);
                        return gson.toJson(new StructuredResponse("ok", "" + sessionKey, null));
                    }
                    else
                    {
                        return gson.toJson(new StructuredResponse("error", "invalid user", null));   
                    }
                }
                else
                {
                    return gson.toJson(new StructuredResponse("error", "invalid user", null));
                }
                // Use or store profile information
                // ...

            } else {
            System.out.println("Invalid ID token.");
            return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            }
        });

        /** 
         * POST route for the "vote machine"
         * key is an entered session key
         * username is the username of the user performing the vote
         * id is the idea_id of the idea being voted on
         * 
         * vote is the value of the vote (true for upvote being pressed, false for downvote)
         * 
         * creates or updates the votes according to their current state and the change applied
         */
        Spark.post("/:key/users/:username/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            String name = request.params("username");
            int id = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                int result;
                if(db.findVote(name, id))
                {
                    result = db.updateVote(name, id, req.vote);
                }
                else
                {
                    result = db.insertVote(name, id, req.vote);
                }
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + name, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * POST route for creating an idea
         * key is an entered session key
         * 
         * uUsername is the username of the user posting the idea
         * mContent is the content of the idea
         * 
         * creates an idea by the given user with the content of mContent
         */
        Spark.post("/:key/messages", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            // NB: createEntry checks for null title and message
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                String base64 = req.mBase64;
                String fID = "";
                try{
                    java.io.File image = new java.io.File(req.filename);
                    java.io.FileWriter writer = new java.io.FileWriter(image);
                    writer.write(base64);
                    writer.close();
                    fID = uploadBasic(image);
                } catch (IOException e){
                    System.out.println(e);
                }
                int newId = db.insertIdea(req.uUsername, req.mContent, fID, req.mLink);      // default to 0 likes
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });
        
        /** 
         * POST route for creating a comment
         * key is an entered session key
         * 
         * uUsername is the username of the user posting the comment
         * mId is the id of the message this comment is being placed on
         * cContent is the content of the comment
         * 
         * creates a comment under the post with id mId by the given user with the
         * content of cContent
         */
        Spark.post("/:key/comments", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                String base64 = req.cBase64;
                String fID = "";
                try{
                    java.io.File image = new java.io.File(req.filename);
                    java.io.FileWriter writer = new java.io.FileWriter(image);
                    writer.write(base64);
                    writer.close();
                    fID = uploadBasic(image);
                } catch (IOException e){
                    System.out.println(e);
                }
                // NB: createEntry checks for null title and message
                int newId = db.insertComment(req.uUsername, req.mId, req.cContent, fID, req.cLink);      // default to 0 likes
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * PUT route for updating users
         * key is an entered session key
         * username is the username of the user being updated
         * 
         * uGI is the users input gender identity
         * uSO is the users input sexual orientation
         * uNote is a note input about the user
         * 
         * updates user information
         */
        Spark.put("/:key/users/:username", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            String name = request.params("username");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                int result = db.updateUser(name, req.uGI, req.uSO, req.uNote);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + name, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });

        /** 
         * PUT route for updating comments
         * key is an entered session key
         * id is the comment_id of the comment being updated
         * 
         * mContent is the new content of the comment
         * 
         * updates a comment's content
         */
        Spark.put("/:key/comments/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            if(HT.containsKey(Integer.parseInt(request.params("key"))))
            {
                /** 
                 * NOTE: mContent should be changed to cContent here, it doesn't stop
                 * anything from functioning but should be changed for the sake of terminalogy,
                 * although that is a big effort task that will also require modifying the
                 * frontends and can be put off until next phase (and really doesn't mean anything)
                 */
                int result = db.updateComment(idx, req.mContent);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            else
            {
                return gson.toJson(new StructuredResponse("error", "Invalid Session Key", null));
            }
        });
    }
    /**
     * Upload new file.
     * 
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public static String uploadBasic(java.io.File file) throws IOException {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.

        
        String CREDS = "{\n"
            + "    \"type\": \"service_account\",\n"
            + "    \"project_id\": \"fiery-glass-405720\",\n"
            + "    \"private_key_id\": \"b4ceb8258153dad9f0187c016b9b10302dcd7274\",\n"
            + "    \"private_key\": \"-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCxA29PAIBCfdAt\nbggw+4SJ1v5q5K0k0uHDJyIthVuNkAEi5Ry6OEGpdpVPoScD3ZYTQ7yJZBW8IYe2\nwaZptJnvbkIS7xQQ6MUdJnu7DNqGzKx6TrTF5Gfl9OtYKP21kL3HD+vr4/x9Dm6Z\n6ZYnZ6ECHL/Cs6tnSWW0uojiJvDVpoxW6Mxwoc9tpYbgaSK3wwVPEcjbt5ITI5OO\nGOhi2Rp1OV3PixymOkzTeABe9qrvBw9pomPFoptcIvX7JEKIJltj/dDxsDR20M78\n0lHqBXLwHDYtqvEkx9jdVSf6NmKQ9ieFO6guyVekyr5IzzzuJO4RmDW1WzVfFa4p\nLV1y0KnPAgMBAAECggEAUKZLexez2FehV2mIEuUtzG2nqkU5PAtW8EbutAXMIlIH\nqoHv1kqWZCMSwV6fu4ukem+EwaZXnsk6H8LYUtYFwsieEylZn6CmWnMzsZQYJo2v\nUeR1SaqYyfWOmrLGcYiaZs1yxJ2x5rqCGt8J2jdFbQRzPD8HK76Nt8u7XSq31OdU\nc6GVvuQVguU+WAUN+0pNuZEuTco27YKUCVUUDoPktOLP1AW/aK/ZJTpva6514qaq\nf1/p1UKWp1YJ9fP6zTRxu++xaxRZFo85omLuq1D7n4YTByA+5h1aai3YyTaWm/ST\nCoFAMr6qPpkUkx4LCMbTOPv37o2HV6pK5zidLWqZsQKBgQDcsabIeBW551Kgjzol\na/s8qS1KP1xswt4r502QJbfDx9s09Gd0+XIfSmcKnJs2subCfFwz0BImKTPbmOS+\n5cN3S5MbTsS5cMIPFJulfdtIIZ6GLNEy9SKtVPZTotHGIgbQDdWnO3QjWs4zr2At\n7thaGXuDPmHe7MSFk/LiueSg/wKBgQDNVOGRswsdLT0kdJsUY9kX8OYEeR+GHE6i\nQ9NGrwvrjJL2PDu9DbCs00cUL317TGgi7v5xUDz3NTv8z0KvJ/I8ZQ0zIxUlcaTD\nNhGeUkADUkSeHyE4SEZQEGUl/qd7QxwnwjDoCB7TtwrwaGWSGsP31JuPSZysC/7f\nuJXZD3knMQKBgFZh4tpMxpRBiwH1jdjf7zLLNUL+kfqwO64Llzx4xvkG3TJZB7Wt\nfZHp2XM54TcDx3cQnjZZlwEA1594tPBTlrK3Dhl+N1ouXIbylgmsvYv6PMZf/HJp\nqO0XeGM8M8fNwcTl5V7T0p2UCWoJlyfjeSOrHcE9RamwGyv9wPAluuuZAoGBAMXM\nuAOgY9t2ggkFX4NT8IU0ppC2kdyilkmQZw4XgLcn00brnWywrHrAiR9z5ECLyWGl\ns207K4/FM4WFr6qGI790ZTPW3v8UK/F1u6E1gL7yWHGucVUDouBr+tSQLYz7iDrw\nwiYz9GZlVV9kYjxOFmw+3qA4/HlskNm5unjAhChxAoGAY1y3+QnkOi+2mCH1qquL\neRNFywap/eX2pwhTr4mOj3fb94ZWI14As0EKaQE5jn71E4OPpaUqZ7zO/qgnLEWf\nJEm98jdZmL8M82xrCmtSgZbCqI7tSdW2nk1BbQzPrrSiNBCtxe9NFpZLjxn5R78X\nyVpZiQ0zJPm+5yE8lUc6wMA=\n-----END PRIVATE KEY-----\n\",\n"
            + "    \"client_email\": \"team-snems-file-storage@fiery-glass-405720.iam.gserviceaccount.com\",\n"
            + "    \"client_id\": \"111794579203975391978\",\n"
            + "    \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
            + "    \"token_uri\": \"https://oauth2.googleapis.com/token\",\n"
            + "    \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
            + "    \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/team-snems-file-storage%40fiery-glass-405720.iam.gserviceaccount.com\",\n"
            + "    \"universe_domain\": \"googleapis.com\"\n"
            + "  }";
        java.io.File credFile = new java.io.File("creds");
        try{
            java.io.FileWriter writer = new java.io.FileWriter(credFile);
            writer.write(CREDS);
            writer.close();
        }
        catch (IOException e){

        }
        System.out.println(CREDS);
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credFile)).createScoped(Collections.singleton(DriveScopes.DRIVE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
    
        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer)
            .setApplicationName("Drive samples")
            .build();

        // Upload file photo.jpg on drive.
        File fileMetadata = new File();
        fileMetadata.setName(file.getName());

        

        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent("image/jpeg", file);
        try {
          File newFile = service.files().create(fileMetadata, mediaContent)
              .setFields("id")
              .execute();
          System.out.println("File ID: " + newFile.getId());
          return newFile.getId();
        } catch (GoogleJsonResponseException e) {
          // TODO(developer) - handle error appropriately
          System.err.println("Unable to upload file: " + e.getDetails());
          throw e;
        }
      }

    /**
     * Get a fully-configured connection to the database, or exit immediately
     * Uses the Postgres configuration from environment variables
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


    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends.  This only needs to be called once.
     * 
     * @param origin the server that is allowed to send requests to this server
     * @param methods the permitted HTTP verbs from the origin
     * @param headers the headers that can be sent with a request from the origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any 
        // get/post/put/delete.  In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}