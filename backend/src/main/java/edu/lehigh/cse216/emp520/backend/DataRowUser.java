package edu.lehigh.cse216.emp520.backend;

import java.util.Date;

/**
 * DataRowUser holds a row of user information.  A row of user information consists of
 * a username, an email, a gender identity, a sexual orientation, a note, and a validity.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class DataRowUser {
    
    /**
     * The unique identifier associated with users.  It's final, because
     * we never want to change it.
     */
    public final String uUsername;

    /**
     * The remaining attributes associated with users
     */
    public final String uEmail; //Does not change

    public String uGI;

    public String uSO;

    public String uNote;

    public boolean uValid;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public final Date mCreated;

    /**
     * Create a new DataRowUser with the provided data, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param username the username to associate with this user
     * @param email the email of the user. Although it is not a primary key it is unique
     * @param GI the user's gender identity
     * @param SO the user's sexual orientation
     * @param note a note about the user
     * @param valid the validity of the user (whether or not they are allowed to sign in)
     */
    DataRowUser(String username, String email, String GI, String SO, String note, boolean valid) {
        uUsername = username;
        uEmail = email;
        uGI = GI;
        uSO = SO;
        uNote = note;
        uValid = valid;
        mCreated = new Date();
    }

    /**
     * Copy constructor to create one DataRowUser from another
     * 
     * @param data the DataRowUser that we want to make a copy of
     */
    DataRowUser(DataRowUser data) {
        uUsername = data.uUsername;
        uEmail = data.uEmail;
        uGI = data.uGI;
        uSO = data.uSO;
        uNote = data.uNote;
        uValid = data.uValid;
        mCreated = data.mCreated;
    }
}