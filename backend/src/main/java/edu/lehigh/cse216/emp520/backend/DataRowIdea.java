package edu.lehigh.cse216.emp520.backend;

import java.util.Date;
import java.util.ArrayList;

/**
 * DataRowIdea holds a row of idea information. A row of idea information consists of
 * an idea_id, the username of the poster, the content, athe numbers of upvotes and downvotes,
 * a list of all associated comments, and a validity.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class DataRowIdea {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public final int mId;

    /**
     * The foreign key for an idea.
     * uUsername connects an idea to it's poster.
     */
    public String uUsername;

    /**
     * The remaining attributes associated with ideas, including their relevant comments
     */
    public String mContent;

    public int mUpvotes;

    public int mDownvotes;

    public boolean mValid;

    public ArrayList<DataRowComment> mComments;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public final Date mCreated;

    public String mFilename;

    public String mBase64;

    public String mLink;

    public String mFileID;

    /**
     * Create a new DataRowIdea with the provided data, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param id the idea_id of the idea
     * @param username the username of the poster of this idea
     * @param content the content of the idea
     * @param upvotes the number of upvotes on the idea
     * @param downvotes the number of downvotes on the idea
     * @param comments the comments associated with this idea
     * @param valid the validity of this idea (whether or not it is allowed to appear in the app)
     */
    DataRowIdea(int id, String username, String content, int upvotes, int downvotes, ArrayList<DataRowComment> comments, boolean valid, String base64, String filename, String link, String fileID) {
        mId = id;
        uUsername = username;
        mContent = content;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mComments = comments;
        mValid = valid;
        mCreated = new Date();
        mBase64 = base64;
        mFilename = filename;
        mLink = link;
        mFileID = fileID;
    }

    /**
     * Copy constructor to create one DataRowIdea from another
     * 
     * @param data the DataRowIdea that we want to make a copy of
     */
    DataRowIdea(DataRowIdea data) {
        mId = data.mId;
        uUsername = data.uUsername;
        mContent = data.mContent;
        mUpvotes = data.mUpvotes;
        mDownvotes = data.mDownvotes;
        mValid = data.mValid;
        mComments = data.mComments;
        mCreated = data.mCreated;
        mFilename = data.mFilename;
        mBase64 = data.mBase64;
        mLink = data.mLink;
        mFileID = data.mFileID;
    }
}