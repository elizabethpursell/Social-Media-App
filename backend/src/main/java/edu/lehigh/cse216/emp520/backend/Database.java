package edu.lehigh.cse216.emp520.backend;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Database creates SQL prepared statements for CRUD commands
 */
public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /**
     * A prepared statement for getting one user from the database
     */
    private PreparedStatement mSelectUser;
    
    /**
     * A prepared statement for getting all users in the database
     */
    private PreparedStatement mSelectAllUsers;

    /**
     * A prepared statement for inserting a user into the database
     */
    private PreparedStatement mInsertUser;

    /**
     * A prepared statement for updating a user in the database
     */
    private PreparedStatement mUpdateUser;

    /**
     * A prepared statement for getting one idea from the database
     */
    private PreparedStatement mSelectIdea;
    
    /**
     * A prepared statement for getting all ideas in the database
     */
    private PreparedStatement mSelectAllIdeas;

    /**
     * A prepared statement for inserting an idea into the database
     */
    private PreparedStatement mInsertIdea;

    /**
     * Prepared statements for updating the vote count of an idea in the database
     */
    private PreparedStatement mUpvoteIdea;
    private PreparedStatement mUnupvoteIdea;
    private PreparedStatement mDownvoteIdea;
    private PreparedStatement mUndownvoteIdea;

    /**
     * A prepared statement for getting all comments made by a user in the database
     */
    private PreparedStatement mSelectCommentsByUser;

    /**
     * A prepared statement for getting all comments under an idea in the database
     */
    private PreparedStatement mSelectCommentsByIdea;

    /**
     * A prepared statement for inserting a comment into the database
     */
    private PreparedStatement mInsertComment;

    /**
     * A prepared statement for updating a comment in the database
     */
    private PreparedStatement mUpdateComment;

    /**
     * A prepared statement for getting all votes made by a user in the database
     */
    private PreparedStatement mSelectVotesByUser;

    /**
     * A prepared statement for getting a specific vote made by a user in the database
     */
    private PreparedStatement mSelectVote;
    
    /**
     * A prepared statement for inserting a vote into the database
     */
    private PreparedStatement mInsertVote;

    /**
     * A prepared statement for updating a vote in the database
     */
    private PreparedStatement mUpdateVote;

    /**
     * A prepared statement for deleting a vote from the database
     */
    private PreparedStatement mDeleteVote;
    
    /**
     * A prepared statement for creating the users table in the database
     */
    private PreparedStatement mCreateTableUsers;

    /**
     * A prepared statement for creating the messages table in the database
     */
    private PreparedStatement mCreateTableIdeas;

    /**
     * A prepared statement for creating the userVotes table in the database
     */
    private PreparedStatement mCreateTableUserVotes;

    /**
     * A prepared statement for creating the comments table in the database
     */
    private PreparedStatement mCreateTableComments;

    /**
     * A prepared statement for dropping the users table in the database
     */
    private PreparedStatement mDropUsers;

    /**
     * A prepared statement for dropping the ideas table in the database
     */
    private PreparedStatement mDropIdeas;

    /**
     * A prepared statement for dropping the comments table in the database
     */
    private PreparedStatement mDropComments;

    /**
     * A prepared statement for dropping the userVotes table in the database
     */
    private PreparedStatement mDropUserVotes;


    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
    * Get a fully-configured connection to the database
    * 
    * @param host The IP address or hostname of the database server
    * @param port The port on the database server to which connection requests should be sent
    * @param path The path to use, can be null
    * @param user The user ID to use when connecting
    * @param pass The password to use when connecting
    * 
    * @return A Database object, or null if we cannot connect properly
    */
    static Database getDatabase(String host, String port, String path, String user, String pass) {
        if( path==null || "".equals(path) ){
            path="/";
        }

        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            String dbUrl = "jdbc:postgresql://" + host + ':' + port + path;
            Connection conn = DriverManager.getConnection(dbUrl, user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        db = db.createPreparedStatements();
        return db;
    } 

    /**
    * Get a fully-configured connection to the database
    * 
    * @param db_url The url to the database
    * @param port_default port to use if absent in db_url
    * 
    * @return A Database object, or null if we cannot connect properly
    */
    static Database getDatabase(String db_url, String port_default) {
        try {
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            String path = dbUri.getPath();
            String port = dbUri.getPort() == -1 ? port_default : Integer.toString(dbUri.getPort());

            return getDatabase(host, port, path, username, password);
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }
    } 

    
    /** 
     * Creates SQL prepared statements for the CRUD commands
     * 
     * @return Database
     */
    public Database createPreparedStatements(){
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblData"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // NotSELECT * FROM "public"."comments" LIMIT 100e: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception
            this.mCreateTableUsers = this.mConnection.prepareStatement(
                    "CREATE TABLE users (username VARCHAR(30) PRIMARY KEY, email VARCHAR(50) NOT NULL UNIQUE, "
                    + "GI VARCHAR(50), SO VARCHAR(50), note VARCHAR(2048), valid boolean NOT NULL DEFAULT true)");
            this.mCreateTableIdeas = this.mConnection.prepareStatement(
                    "CREATE TABLE ideas (idea_id SERIAL PRIMARY KEY, username VARCHAR(30) NOT NULL, content VARCHAR(2048) "
                    + "NOT NULL, upvotes int NOT NULL, downvotes int NOT NULL, valid boolean NOT NULL DEFAULT true, FOREIGN KEY (username) REFERENCES users(username))");
            this.mCreateTableComments = this.mConnection.prepareStatement(
                    "CREATE TABLE comments (comment_id SERIAL PRIMARY KEY, idea_id int NOT NULL, username VARCHAR(30) NOT NULL, "
                    + " content VARCHAR(2048), FOREIGN KEY (username) REFERENCES users(username), FOREIGN KEY (idea_id) REFERENCES ideas(idea_id))");
            this.mCreateTableUserVotes = this.mConnection.prepareStatement(
                    "CREATE TABLE userVotes (idea_id int NOT NULL, username VARCHAR(30) NOT NULL, vote boolean NOT NULL, "
                    + " CONSTRAINT uservote PRIMARY KEY (idea_id,username), FOREIGN KEY (username) REFERENCES users(username), FOREIGN KEY (idea_id) REFERENCES ideas(idea_id))");
            this.mDropUsers = this.mConnection.prepareStatement("DROP TABLE users");
            this.mDropIdeas = this.mConnection.prepareStatement("DROP TABLE messages");
            this.mDropComments = this.mConnection.prepareStatement("DROP TABLE comments");
            this.mDropUserVotes = this.mConnection.prepareStatement("DROP TABLE userVotes");

            // Standard CRUD operations
            
            // Users Table SQL
            this.mSelectUser = this.mConnection.prepareStatement("SELECT * from users WHERE username=?");
            this.mSelectAllUsers = this.mConnection.prepareStatement("SELECT * FROM users ORDER BY (username)");
            this.mInsertUser = this.mConnection.prepareStatement("INSERT into users VALUES (?, ?, ?, ?, ?, default)");
            this.mUpdateUser = this.mConnection.prepareStatement("UPDATE users SET GI = ?, SO = ?, note = ? WHERE username = ?");
            
            // Ideas Table SQL
            this.mSelectIdea = this.mConnection.prepareStatement("SELECT * from ideas WHERE idea_id=?");
            this.mSelectAllIdeas = this.mConnection.prepareStatement("SELECT * FROM ideas WHERE valid=true ORDER BY (idea_id)");
            this.mInsertIdea = this.mConnection.prepareStatement("INSERT INTO ideas VALUES (default, ?, ?, 0, 0, default, ?, ?)");
            this.mUpvoteIdea = this.mConnection.prepareStatement("UPDATE ideas SET upvotes = upvotes + 1 WHERE idea_id = ?");
            this.mUnupvoteIdea = this.mConnection.prepareStatement("UPDATE ideas SET upvotes = upvotes - 1 WHERE idea_id = ?");
            this.mDownvoteIdea = this.mConnection.prepareStatement("UPDATE ideas SET downvotes = downvotes + 1 WHERE idea_id = ?");
            this.mUndownvoteIdea = this.mConnection.prepareStatement("UPDATE ideas SET downvotes = downvotes - 1 WHERE idea_id = ?");
            
            // Comments Table SQL
            this.mSelectCommentsByUser = this.mConnection.prepareStatement("SELECT * from comments WHERE username=?");
            this.mSelectCommentsByIdea = this.mConnection.prepareStatement("SELECT * from comments WHERE idea_id=?");
            this.mInsertComment = this.mConnection.prepareStatement("INSERT into comments VALUES (default, ?, ?, ?, ?, ?)");
            this.mUpdateComment = this.mConnection.prepareStatement("UPDATE comments SET content = ? WHERE comment_id = ?");
            
            // UserVotes Table SQL
            this.mSelectVote = this.mConnection.prepareStatement("SELECT vote from userVotes WHERE username = ? and idea_id = ?");
            this.mSelectVotesByUser = this.mConnection.prepareStatement("SELECT * from userVotes WHERE username=?");
            this.mInsertVote = this.mConnection.prepareStatement("INSERT INTO userVotes VALUES (?, ?, ?)");
            this.mUpdateVote = this.mConnection.prepareStatement("UPDATE userVotes SET vote = ? WHERE username = ? and idea_id = ?");
            this.mDeleteVote = this.mConnection.prepareStatement("DELETE FROM userVotes WHERE username = ? and idea_id = ?");
            

            
            return this;
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            this.disconnect();
            return null;
        }
    }

    /**
     * Close the current connection to the database, if one exists.
     * The database will be null after this is executed.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Get all data for a specific user, by username
     * 
     * @param username the username of the user being requested
     * 
     * @return the data for the requested user, or null if the ID was invalid
     */
    DataRowUser selectUser(String username) {
        DataRowUser res = null;
        try {
            mSelectUser.setString(1, username);
            ResultSet rs = mSelectUser.executeQuery();
            if (rs.next()) {
                res = new DataRowUser(rs.getString("username"), rs.getString("email"), rs.getString("GI"), rs.getString("SO"), rs.getString("note"), rs.getBoolean("valid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for all the users
     * 
     * @return the data for the users
     */
    ArrayList<DataRowUser> selectAllUsers() {
        ArrayList<DataRowUser> res = new ArrayList<DataRowUser>();
        try {
            ResultSet rs = mSelectAllUsers.executeQuery();
            while (rs.next()) {
                res.add(new DataRowUser(rs.getString("username"), rs.getString("email"), rs.getString("GI"), rs.getString("SO"), rs.getString("note"), rs.getBoolean("valid")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Insert a user into the database
     * 
     * @param username the username of the user
     * @param email the email of the user
     * @param GI the gender identity of the user
     * @param SO the sexual orientation of the user
     * @param note the note of the user
     * 
     * @return The number of rows that were inserted
     */
    int insertUser(String username, String email, String GI, String SO, String note) {
        int count = 0;
        try {
            mInsertUser.setString(1, username);
            mInsertUser.setString(2, email);
            mInsertUser.setString(3, GI);
            mInsertUser.setString(4, SO);
            mInsertUser.setString(5, note);
            count += mInsertUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update a user in the database
     * 
     * @param username the username of the user
     * @param GI the gender identity of the user
     * @param SO the sexual orientation of the user
     * @param note the note of the user
     * 
     * @return the number of rows that were updated
     */
    int updateUser(String username, String GI, String SO, String note) {
        int count = 0;
        try {
            mUpdateUser.setString(1, GI);
            mUpdateUser.setString(2, SO);
            mUpdateUser.setString(3, note);
            mUpdateUser.setString(4, username);
            count += mUpdateUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Checks if a user exists in the database
     * 
     * @param username the username of the user
     * 
     * @return true if the user exists, false otherwise
     */
    boolean findUser(String username)
    {
        try {
            mSelectUser.setString(1, username);
            ResultSet rs = mSelectUser.executeQuery();
            return (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a user is valid in the database
     * 
     * @param username the username of the user
     * 
     * @return true if the user is valid, false otherwise
     */
    boolean checkValidUser(String username)
    {
        try {
            mSelectUser.setString(1, username);
            ResultSet rs = mSelectUser.executeQuery();
            rs.next();
            return rs.getBoolean("valid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all data for a specific post, by ID
     * 
     * @param id the idea_id of the row being requested
     * 
     * @return the data for the requested idea (with all comments made on that idea), or null if the ID was invalid
     */
    DataRowIdea selectIdea(int id) {
        DataRowIdea res = null;
        try {
            mSelectIdea.setInt(1, id);
            ResultSet rs = mSelectIdea.executeQuery();
            if (rs.next()) {
                int idx = rs.getInt("idea_id");
                ArrayList<DataRowComment> res2 = selectCommentsByIdea(id);
                res = new DataRowIdea(idx, rs.getString("username"), rs.getString("content"), rs.getInt("upvotes"), rs.getInt("downvotes"), res2, rs.getBoolean("valid"), rs.getString("file_id"), "", rs.getString("link"), rs.getString("file_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * Get all data for all ideas
     * 
     * @return the data for all the ideas (each with all comments made on that idea), as an ArrayList
     */
    ArrayList<DataRowIdea> selectAllIdeas() {
        ArrayList<DataRowIdea> res = new ArrayList<DataRowIdea>();
        try {
            ResultSet rs = mSelectAllIdeas.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("idea_id");
                // ArrayList<DataRowComment> res2 = selectCommentsByIdea(id);
                res.add(new DataRowIdea(id, rs.getString("username"), rs.getString("content"), rs.getInt("upvotes"), rs.getInt("downvotes"), new ArrayList<DataRowComment>() , rs.getBoolean("valid"), rs.getString("file_id"), "", rs.getString("link"), rs.getString("file_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Insert a post into the database
     * 
     * @param username the username of the poster
     * @param content the message for this new idea
     * 
     * @return the number of rows that were inserted
     */
    int insertIdea(String username, String content, String fileID, String link) {
        int count = 0;
        try {
            mInsertIdea.setString(1, username);
            mInsertIdea.setString(2, content);
            mInsertIdea.setString(3, fileID);
            mInsertIdea.setString(4, link);
            count += mInsertIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get all data for all comments made by a specific user, by username
     * 
     * @param username the username of the commenter
     * 
     * @return the data for each comment made by the specified user, or null if the username was invalid
     */
    ArrayList<DataRowComment> selectCommentsByUser(String username) {
        ArrayList<DataRowComment> res = new ArrayList<DataRowComment>();
        try {
            mSelectCommentsByUser.setString(1, username);
            ResultSet rs = mSelectCommentsByUser.executeQuery();
            while (rs.next()) {
                res.add(new DataRowComment(rs.getInt("comment_id"), rs.getString("username"), rs.getInt("idea_id"), rs.getString("content"), rs.getString("file_id"), "", rs.getString("link"), rs.getString("file_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for all comments made under a specific idea, by id
     * 
     * @param id the idea_id of the idea the comments fall under
     * 
     * @return the data for each comment under the specified idea, or null if the idea_id was invalid
     */
    ArrayList<DataRowComment> selectCommentsByIdea(int id) {
        ArrayList<DataRowComment> res = new ArrayList<DataRowComment>();
        try {
            mSelectCommentsByIdea.setInt(1, id);
            ResultSet rs = mSelectCommentsByIdea.executeQuery();
            while (rs.next()) {
                res.add(new DataRowComment(rs.getInt("comment_id"), rs.getString("username"), rs.getInt("idea_id"), rs.getString("content"), rs.getString("file_id"), "", rs.getString("link"), rs.getString("file_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Insert a comment into the database
     * 
     * @param username the username of the commenter
     * @param id the idea_id of the idea this comment is under
     * @param content the message for this new comment
     * 
     * @return the number of rows that were inserted
     */
    int insertComment(String username, int id, String content, String fileID, String link) {
        int count = 0;
        try {
            mInsertComment.setInt(1, id);
            mInsertComment.setString(2, username);
            mInsertComment.setString(3, content);
            mInsertComment.setString(4, fileID);
            mInsertComment.setString(5, link);
            count += mInsertComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update a comment in the database
     * 
     * @param id the comment_id of the comment
     * @param content the new message for this comment
     * 
     * @return the number of rows that were updated
     */
    int updateComment(int id, String content) {
        int res = -1;
        try {
            mUpdateComment.setString(1, content);
            mUpdateComment.setInt(2, id);
            res = mUpdateComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for all votes made by a specific user, by username
     * 
     * @param username the username of the voter
     * 
     * @return the data for each vote made by the specified user, or null if the username was invalid
     */
    ArrayList<DataRowVote> selectVotesByUser(String username) {
        ArrayList<DataRowVote> res = new ArrayList<DataRowVote>();
        try {
            mSelectVotesByUser.setString(1, username);
            ResultSet rs = mSelectVotesByUser.executeQuery();
            while (rs.next()) {
                res.add(new DataRowVote(rs.getString("username"), rs.getInt("idea_id"), rs.getBoolean("vote")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Insert a comment into the database
     * 
     * @param username the username of the voter
     * @param id the idea_id of the idea that is being voted on
     * @param vote the vote button that was pressed (true for upvotes, false for downvotes)
     * 
     * @return the number of rows that were inserted
     */
    int insertVote(String username, int id, boolean vote) {
        int count = 0;
        try {
            mInsertVote.setInt(1, id);
            mInsertVote.setString(2, username);
            mInsertVote.setBoolean(3, vote);
            count += mInsertVote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(vote)
            {
                mUpvoteIdea.setInt(1, id);
                count += mUpvoteIdea.executeUpdate();
            }
            else
            {
                mDownvoteIdea.setInt(1, id);
                count += mDownvoteIdea.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Updates a comment into the database
     * 
     * @param username the username of the voter
     * @param id the idea_id of the idea that is being voted on
     * @param vote the vote button that was pressed (true for upvotes, false for downvotes)
     * 
     * @return the number of rows that were updated/deleted
     */
    int updateVote(String username, int id, boolean vote)
    {
        int count = 0;
        try {
            mSelectVote.setInt(2, id);
            mSelectVote.setString(1, username);
            ResultSet rs = mSelectVote.executeQuery();
            rs.next();
            boolean currentVote = rs.getBoolean("vote");
            if(vote && currentVote)
            {
                mDeleteVote.setString(1, username);
                mDeleteVote.setInt(2, id);
                count += mDeleteVote.executeUpdate();
                mUnupvoteIdea.setInt(1, id);
                count += mUnupvoteIdea.executeUpdate();
            } else if(!vote && !currentVote)
            {
                mDeleteVote.setString(1, username);
                mDeleteVote.setInt(2, id);
                count += mDeleteVote.executeUpdate();
                mUndownvoteIdea.setInt(1, id);
                count += mUndownvoteIdea.executeUpdate();
            } else if(vote && !currentVote)
            {
                mUpdateVote.setBoolean(1, vote);
                mUpdateVote.setString(2, username);
                mUpdateVote.setInt(3, id);
                count += mUpdateVote.executeUpdate();
                mUndownvoteIdea.setInt(1, id);
                count += mUndownvoteIdea.executeUpdate();
                mUpvoteIdea.setInt(1, id);
                count += mUpvoteIdea.executeUpdate();
            } else if(!vote && currentVote)
            {
                mUpdateVote.setBoolean(1, vote);
                mUpdateVote.setString(2, username);
                mUpdateVote.setInt(3, id);
                count += mUpdateVote.executeUpdate();
                mUnupvoteIdea.setInt(1, id);
                count += mUnupvoteIdea.executeUpdate();
                mDownvoteIdea.setInt(1, id);
                count += mDownvoteIdea.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Checks if a vote exists in the database
     * 
     * @param username the username of the voter
     * @param id the idea_id of the idea that is being voted on
     * 
     * @return true if the vote exists, false otherwise
     */
    boolean findVote(String username, int id)
    {
        try {
            mSelectVote.setInt(2, id);
            mSelectVote.setString(1, username);
            ResultSet rs = mSelectVote.executeQuery();
            return (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create users table. If it already exists, this will print an error
     */
    void createTableUsers() {
        try {
            mCreateTableUsers.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create ideas table. If it already exists, this will print an error
     */
    void createTableIdeas() {
        try {
            mCreateTableIdeas.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create comments table. If it already exists, this will print an error
     */
    void CreateTableComments() {
        try {
            mCreateTableComments.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create userVotes table. If it already exists, this will print an error
     */
    void createTableUserVotes() {
        try {
            mCreateTableUserVotes.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove users table from the database. If it does not exist, this will print an error.
     */
    void dropTableUsers() {
        try {
            mDropUsers.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove ideas table from the database. If it does not exist, this will print an error.
     */
    void dropTableIdeas() {
        try {
            mDropIdeas.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove comments table from the database. If it does not exist, this will print an error.
     */
    void dropTableComments() {
        try {
            mDropComments.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove userVotes table from the database. If it does not exist, this will print an error.
     */
    void dropTableUserVotes() {
        try {
            mDropUserVotes.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}