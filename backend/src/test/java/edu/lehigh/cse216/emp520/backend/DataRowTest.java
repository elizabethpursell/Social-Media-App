package edu.lehigh.cse216.emp520.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class DataRowTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DataRowTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DataRowTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the user it
     * creates
     */
    public void testConstructorUser() {
        String name = "userguy";
        String email = "userguy@lehigh.edu";
        String GI = "a";
        String SO = "b";
        String note = "userguy note";
        boolean valid = true;
        DataRowUser d = new DataRowUser(name, email, GI, SO, note, valid);

        assertTrue(d.uUsername.equals(name));
        assertTrue(d.uEmail.equals(email));
        assertTrue(d.uGI.equals(GI));
        assertTrue(d.uSO.equals(SO));
        assertTrue(d.uNote.equals(note));
        assertTrue(d.uValid == valid);
        assertFalse(d.mCreated == null);
    }

    /**
     * Ensure that the constructor populates every field of the idea it
     * creates
    */
    public void testConstructorIdea() {
        String content = "Test Content";
        int upvotes = 12;
        int downvotes = 2;
        int id = 17;
        String user = "userguy";
        boolean valid = true;
        String base64 = "kjfahkfajb";
        String filename = "woopty.jpeg";
        String link = "thatwebsiteifound.com";
        String fileID = "nvlwieufkjbgiusbfwoieo";
        ArrayList<DataRowComment> comments = new ArrayList<DataRowComment>();
        DataRowIdea d = new DataRowIdea(id, user, content, upvotes, downvotes, comments, valid, base64, filename, link, fileID);

        assertTrue(d.mContent.equals(content));
        assertTrue(d.mUpvotes == upvotes);
        assertTrue(d.mDownvotes == downvotes);
        assertTrue(d.uUsername.equals(user));
        assertTrue(d.mId == id);
        assertTrue(d.mComments.equals(comments));
        assertTrue(d.mValid == valid);
        assertFalse(d.mCreated == null);
        assertTrue(d.mBase64.equals(base64));
        assertTrue(d.mFilename.equals(filename));
        assertTrue(d.mLink.equals(link));
        assertTrue(d.mFileID.equals(fileID));
    }

    /**
     * Ensure that the constructor populates every field of the comments it
     * creates
    */ 
    public void testConstructorComments() {
        String name = "userguy";
        String content = "Userguy Comment";
        int id = 17;
        int cid = 1;
        String base64 = "kjfahkfajb";
        String filename = "woopty.jpeg";
        String link = "thatwebsiteifound.com";
        String fileID = "nvlwieufkjbgiusbfwoieo";
        DataRowComment d = new DataRowComment(cid, name, id, content, base64, filename, link, fileID);

        assertTrue(d.uUsername.equals(name));
        assertTrue(d.cContent.equals(content));
        assertTrue(d.cId == cid);
        assertTrue(d.mId == id);
        assertFalse(d.mCreated == null);
        assertTrue(d.cBase64.equals(base64));
        assertTrue(d.cFilename.equals(filename));
        assertTrue(d.cLink.equals(link));
        assertTrue(d.cFileID.equals(fileID));
    }

    /**
     * Ensure that the constructor populates every field of the userVotes it
     * creates
     */
    public void testConstructorVotes() {
        String name = "userguy";
        int id = 17;
        boolean vote = true;
        DataRowVote d = new DataRowVote(name, id, vote);

        assertTrue(d.uUsername.equals(name));
        assertTrue(d.mId == id);
        assertTrue(d.uVote == vote);
        assertFalse(d.mCreated == null);
    }

    /**
     * Ensure that the copy constructor for ideas works correctly
    */ 
    public void testCopyConstructorIdea() {
        String content = "Test Content For Copy";
        int upvotes = 12;
        int downvotes = 2;
        int id = 17;
        String user = "userguy";
        boolean valid = true;
        String base64 = "kjfahkfajb";
        String filename = "woopty.jpeg";
        String link = "thatwebsiteifound.com";
        String fileID = "nvlwieufkjbgiusbfwoieo";
        ArrayList<DataRowComment> comments = new ArrayList<DataRowComment>();
        DataRowIdea d = new DataRowIdea(id, user, content, upvotes, downvotes, comments, valid, base64, filename, link, fileID);
        DataRowIdea d2 = new DataRowIdea(d);
        
        assertTrue(d2.mContent.equals(d.mContent));
        assertTrue(d2.mUpvotes == d.mUpvotes);
        assertTrue(d2.mDownvotes == d.mDownvotes);
        assertTrue(d2.uUsername.equals(d.uUsername));
        assertTrue(d2.mId == d.mId);
        assertTrue(d2.mComments.equals(d.mComments));
        assertTrue(d2.mValid == d.mValid);
        assertTrue(d2.mCreated.equals(d.mCreated));
        assertTrue(d.mBase64.equals(base64));
        assertTrue(d.mFilename.equals(filename));
        assertTrue(d.mLink.equals(link));
        assertTrue(d.mFileID.equals(fileID));
    }

    /**
     * Ensure that the copy constructor for users works correctly
     */
    public void testCopyConstructorUser() {
        String name = "userguy";
        String email = "userguy@lehigh.edu";
        String GI = "a";
        String SO = "b";
        String note = "userguy note";
        boolean valid = true;
        DataRowUser d = new DataRowUser(name, email, GI, SO, note, valid);
        DataRowUser d2 = new DataRowUser(d);

        assertTrue(d2.uUsername.equals(d.uUsername));
        assertTrue(d2.uEmail.equals(d.uEmail));
        assertTrue(d2.uGI.equals(d.uGI));
        assertTrue(d2.uSO.equals(d.uSO));
        assertTrue(d2.uNote.equals(d.uNote));
        assertTrue(d2.uValid == d.uValid);
        assertFalse(d2.mCreated == null);
    }

    /**
     * Ensure that the copy constructor for comments works correctly
    */ 
    public void testCopyConstructorComments() {
        String name = "userguy";
        String content = "Userguy Comment";
        int id = 17;
        int cid = 1;
        String base64 = "kjfahkfajb";
        String filename = "woopty.jpeg";
        String link = "thatwebsiteifound.com";
        String fileID = "nvlwieufkjbgiusbfwoieo";
        DataRowComment d = new DataRowComment(cid, name, id, content, base64, filename, link, fileID);
        DataRowComment d2 = new DataRowComment(d);

        assertTrue(d2.uUsername.equals(d.uUsername));
        assertTrue(d2.cContent.equals(d.cContent));
        assertTrue(d2.cId == d.cId);
        assertTrue(d2.mId == d.cId);
        assertFalse(d2.mCreated == null);
        assertTrue(d2.cBase64.equals(d.cBase64));
        assertTrue(d2.cFilename.equals(d.cFilename));
        assertTrue(d2.cLink.equals(d.cLink));
        assertTrue(d2.cFileID.equals(d.cFileID));
    }

    /**
     * Ensure that the copy constructor for votes works correctly
     */
    public void testVotesConstructorVotes() {
        String name = "userguy";
        int id = 17;
        boolean vote = true;
        DataRowVote d = new DataRowVote(name, id, vote);
        DataRowVote d2 = new DataRowVote(d);

        assertTrue(d2.uUsername.equals(d.uUsername));
        assertTrue(d2.mId == d.mId);
        assertTrue(d2.uVote == d.uVote);
        assertFalse(d2.mCreated == null);
    }
}