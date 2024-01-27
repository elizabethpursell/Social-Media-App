package edu.lehigh.cse216.emp520.backend;

import java.util.Date;

/**
 * DataRowComment holds a row of comment information. A row of comment information consists of
 * a comment_id, the username of the commenter, the idea_id of the idea the comment is on,
 * and the content.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class DataRowComment {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public final int cId;

    /**
     * The foreign keys for a comment.
     * uUsername connects a comment to it's poster.
     * mId connects a comment to the idea it is under.
     */
    public String uUsername;

    public int mId;

    /**
     * The remaining attribute associated with comments, the content
     */
    public String cContent;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public final Date mCreated;

    public String cLink;

    public String cBase64;

    public String cFilename;

    public String cFileID;

    /**
     * Create a new DataRowIdea with the provided data, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param id the comment_id of the comment
     * @param username the username of the poster of this idea
     * @param id2 the idea_id of the post this comment is under
     * @param content the content of the idea
     */
    DataRowComment(int id, String username, int id2, String content, String base64, String filename, String link, String fileID) {
        cId = id;
        uUsername = username;
        mId = id2;
        cContent = content;
        mCreated = new Date();
        cBase64 = base64;
        cFilename = filename;
        cLink = link;
        cFileID = fileID;
    }

    /**
     * Copy constructor to create one DataRowComment from another
     * 
     * @param data the DataRowComment that we want to make a copy of
     */
    DataRowComment(DataRowComment data) {
        cId = data.cId;
        uUsername = data.uUsername;
        mId = data.mId;
        cContent = data.cContent;
        mCreated = data.mCreated;
        cBase64 = data.cBase64;
        cFilename = data.cFilename;
        cLink = data.cLink;
        cFileID = data.cFileID;
    }
}