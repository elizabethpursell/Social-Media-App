package edu.lehigh.cse216.emp520.backend;

import java.util.Date;

/**
 * DataRowVote holds a row of userVote information. A row of vote information consists of
 * the username of the voter, the idea_id that is being voted on, and the vote.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class DataRowVote {
    /**
     * The unique identifier associated with this element. It's final, because
     * we never want to change it. Both foreign keys, uUsername and mId,
     * are required to differentiate.
     */
    public final String uUsername;

    public final int mId;

    /**
     * The remaining attribute associated with userVotes, the vote
     */
    public boolean uVote;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public final Date mCreated;

    /**
     * Create a new DataRowVote with the provided data, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     *
     * @param username the username of the poster of this idea
     * @param id the idea_id of the post this comment is under
     * @param vote the value of the vote (true for upvotes, false for downvotes)
     */
    DataRowVote(String username, int id, boolean vote) {
        uUsername = username;
        mId = id;
        uVote = vote;
        mCreated = new Date();
    }

    /**
     * Copy constructor to create one DataRowVote from another
     * 
     * @param data the DataRowVote that we want to make a copy of
     */
    DataRowVote(DataRowVote data) {
        uUsername = data.uUsername;
        mId = data.mId;
        uVote = data.uVote;
        mCreated = data.mCreated;
    }
}