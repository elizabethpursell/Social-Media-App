package edu.lehigh.cse216.emp520.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server. it contains all of the possible attributes that could be send or posted.
 * Most of these are found in the data row classes where they represent the respective
 * attributes, however one big class can be used to represent requests.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {

    public String mContent;

    public int mUpvotes;

    public int mDownvotes;

    public boolean mValid;

    public boolean cValid;

    public boolean vote;

    public String uUsername;

    public String uEmail;

    public String uGI;

    public String uSO;

    public String uNote;

    public int mId;

    public int cId;

    public String cContent;

    public String idtoken;

    public String mBase64;

    public String cBase64;

    public String filename;

    public String mLink;

    public String cLink;
}