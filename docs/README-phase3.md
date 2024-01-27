# Phase 3 Documentation

## User Stories

### Authenticated User Stories

1.  
    * As **an authenticated user**
    * I want to **see posts**
    * So I can **experience the thoughts of others**
2.  
    * As **an authenticated user**
    * I want to **create a post**
    * So I can **share my thoughts**
3.  
    * As **an authenticated user**
    * I want to **attach images, links, and other files to my posts**
    * So I can **share a greater amount of information**
4.  
    * As **an authenticated user**
    * I want to **upvote a post**
    * So I can **express my agreement**
5.  
    * As **an authenticated user**
    * I want to **downvote a post**
    * So I can **express my disagreement**
6.  
    * As **an authenticated user**
    * I want to **comment on a post**
    * So I can **express my opinion of the post**
7.  
    * As **an authenticated user**
    * I want to **edit my comment on a post**
    * So I can **express a change in opinion or fix typos**
8.  
    * As **an authenticated user**
    * I want to **undo an upvote or downvote**
    * So I can **express a change in opinion or undo an accidental up/downvote**
9.  
    * As **an authenticated user**
    * I want to **view and edit my profile**
    * So I can **make sure that the information is correct**
10.  
    * As **an authenticated user**
    * I want to **take a photo and upload it with the mobile app**
    * So I can **take and share new photos easily**

### Admin User Stories

1.  
    * As **an admin**
    * I want to **create tables**
    * So I can **add tables to store posts, comments, users, and the relations between them**
2.  
    * As **an admin**
    * I want to **delete tables**
    * So I can **remove incorrect or unneeded clutter from the database to save space**
3.  
    * As **an admin**
    * I want to **prepopulate tables**
    * So I and other developers can **test for functionality and ensure that the app runs properly**
4.  
    * As **an admin**
    * I want to **invalidate users or posts**
    * So I can **remove users and posts that are not appropriate**
5.  
    * As **an admin**
    * I want to **manage the file storage system**
    * So I can **remove older files and large files to conserve space**

## Tests for User Stories

**Manual Test for User Story**

1. To manually test that posts can be seen, open on of the frontends and check that it displays the posts from the database
2. To manually test that you can create a post, press the add post, type in a post, and click post, and see if it appears in the post list and in the database
3. To manually test upvotes and downvotes, click the upvote/downvote button and see if it has the correct impact on that post's upvote/downvote count in the database
4. To manually test comments, add a comment, see if it appears on screen and in the database, then edit it and see if the update appears
5. To manually test user profiles, add a user, see if it appears on screen and in the database, then edit it and see if the update appears
6. To manually test files are appearing, open one of the frontends and check to see if files can be accessed
7. To manually test that the user can take photos with the mobile app, use the mobile app to take a photo and check the data storage to see if the image has been stored

Testing the admin user stories is similar to above, but instead you perform the actions from the admin commandline app (so, for example, run the command to prepopulate tables, then see if the generated table appears in the database)

Automatic testing is done in the format of testing we used for phase 1, such as using jasmine to test the web frontend, npm test for the backend/admin, etc.

**Automated Test for User Story**  
1. Select the right test automation tool, such as Testsigma  
2. Write the scripts for each test cases from manuel test for user stories  
3. Run the script to see if each button is functioning properly.  

We can also write tests for the backend using mvn test, frontend using jasmine, mobile using flutter test, etc. to make sure that the app can successfully meet the user stores

## Drawing of System
* [System Diagram](Phase-3/system-diagram.png)

## Drawings of Web/Mobile User Interfaces
* [Web UI](Phase-3/Web UI.png)
* [Mobile UI](Phase-3/mobile_ui.png)

## Drawing of State Machine

* [User State Machine](Phase-3/user-state-machine.png)
* [Idea State Machine](Phase-3/idea-state-machine.png)

## Routes
### GET /key/messages
Purpose: Gets the ids, content, comments, files, links, and upvotes/downvotes for all messages
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/messages -X GET
```
#### Response
```
{
    "mStatus":"ok",
    "mData":[
        {
            "mId":0,
            "mContent":"This is a test post",
            "mUpvotes":4,
            "mDownvotes": 1000,
            "mBase64": "szdfgjhgfdz",
            "filename": "thing.jpg",
            "mLink": https://thing.com,
            "uUsername": "User guy",
            "mCreated":"Oct 7, 2023, 9:01:46 PM",
            "mComments":[
                {
                    "cId": 0,
                    "uUsername": "User guy",
                    "cContent": "This post sucks",
                    "cBase64": "szdfgjhgfdz",
                    "filename": "thing.jpg",
                    "cLink": https://thing.com
                }, {
                    "cId": 1,
                    "uUsername": "User guy 2",
                    "cContent": "This post does not suck"
                    "cBase64": "szdfgjhgfdz",
                    "filename": "thing.jpg",
                    "cLink": https://thing.com
                }
            ]
        }, {
            "mId":2,
            "mContent":"Testing 2",
            "mUpvotes":4,
            "mDownvotes": 1000,
            "mBase64": "szdfgjhgfdz",
            "filename": "thing.jpg",
            "mLink": https://place.net,
            "uUsername": "User guy",
            "mCreated":"Oct 7, 2023, 9:01:46 PM",
            "mComments":[
                {
                    "cId": 2,
                    "uUsername": "User guy",
                    "cContent": "This post sucks"
                    "cBase64": "szdfgjhgfdz",
                    "filename": "thing.jpg",
                    "cLink": https://thing.com
                }, {
                    "cId": 3,
                    "uUsername": "User guy 2",
                    "cContent": "This post does not suck"
                    "cBase64": "szdfgjhgfdz",
                    "filename": "thing.jpg",
                    "cLink": https://thing.com
                }
            ]
        }
    ]
}
```
### GET /key/messages/id
Purpose: Gets the id, content, comments, and upvotes/downvotes for one message
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/messages/0 -X GET
```
#### Response
```
{
    "mStatus":"ok",
    "mData": {
        "mId":0,
        "mContent":"This is a test post",
        "mUpvotes":4,
        "mDownvotes": 1000,
        "mBase64": "szdfgjhgfdz",
        "filename": "thing.jpg",
        "mLink": https://thing.com,
        "uUsername": "User guy",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
        "mComments":[
            {
                "cId": 0,
                "uUser": "User guy",
                "cContent": "This post sucks"
                "cBase64": "szdfgjhgfdz",
                "filename": "thing.jpg",
                "cLink": https://thing.com
            }, {
                "cId": 1,
                "uUser": "User guy 2",
                "cContent": "This post does not suck"
                "cBase64": "szdfgjhgfdz",
                "filename": "thing.jpg",
                "cLink": https://thing.com
            }
        ]
    }
}
```
### GET /users ** NOT USED **
Purpose: Gets the username, email, GI, SO, and note of all users
```
curl -s https://team-snems.dokku.cse.lehigh.edu/users -X GET -d
```
#### Request
```
{
    'token':1
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData": [
    {
        "uUsername":"userguy",
        "uEmail":"userguy@lehigh.edu",
        "uGI": "guy",
        "uSO": "N/A",
        "uNote": "werstdhfgjklgfsdatm,lj.;lkugyftdredsawAsdrsgftghjfds",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    },
    {
        "uUsername":"userguy2",
        "uEmail":"userguy2@lehigh.edu",
        "uGI": "guy",
        "uSO": "N/A",
        "uNote": "werstdhfgjklgfsdatm,lj.;lkugyftdredsawAsdrsgftghjfds",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    }]
}
```
### GET /users/username ** NOT USED **
Purpose: Gets the username, email, GI, SO, and note of the user
```
curl -s https://team-snems.dokku.cse.lehigh.edu/users/userguy -X GET
```
#### Request
```
{
    'token':1
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData": {
        "uUsername":"userguy",
        "uEmail":"userguy@lehigh.edu",
        "uGI": "guy",
        "uSO": "N/A",
        "uNote": "werstdhfgjklgfsdatm,lj.;lkugyftdredsawAsdrsgftghjfds",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    }
}
```
### GET /users/username/comments ** NOT USED **
Purpose: Gets all the comments of a specific user
```
curl -s https://team-snems.dokku.cse.lehigh.edu/users/userguy/comments -X GET
```
#### Request
```
{
    'token':1
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData": [
    {
        "cId": 1,
        "cFileId": 12347,
        "cLink": https://thing.com,
        "uUsername":"userguy",
        "mId": 1,
        "cContent": "this is a comment",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    },
    {
        "cId": 2,
        "mFileId": 12349,
        "mLink": null,
        "uUsername":"userguy",
        "mId": 2,
        "cContent": "this is also a comment",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    }]
}
```
### GET /key/messages/id/comments
Purpose: Gets all the comments of a specific post (mId)
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/messages/1/comments -X GET
```
#### Response
```
{
    "mStatus":"ok",
    "mData": [
    {
        "cId": 1,
        "cBase64": "szdfgjhgfdz",
        "filename": "thing.jpg",
        "cLink": https://thing.com,
        "uUsername":"userguy",
        "mId": 1,
        "cContent": "this is a comment",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    },
    {
        "cId": 2,
        "cBase64": "szdfgjhgfdz",
        "filename": "thing.jpg",
        "cLink": https://google.com,
        "uUsername":"emp520",
        "mId": 1,
        "cContent": "this is also a comment",
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    }]
}
```
### GET /users/username/votes ** NOT USED **
Purpose: Gets all the votes of a specific user (vote == true when upvoted, vote == false when downvoted, if no entry exists for a specific post then the user didn't vote on that post)
```
curl -s https://team-snems.dokku.cse.lehigh.edu/users/userguy/votes -X GET
```
#### Request
```
{
    'token':1
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData": [
    {
        "uUsername":"userguy",
        "mId": 1,
        "uVote": true,
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    },
    {
        "uUsername":"userguy",
        "mId": 2,
        "uVote": false,
        "mCreated":"Oct 8, 2023 12:40:48 AM",
    }]
}
```

### POST /key/messages
Purpose: Inserts a new post with specific user and content; Defaults to 0 upvotes/downvotes
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/messages -X POST -d "{'mContent':'testing', "uUsername": "User guy", "mBase64": "afdghjgfdsda", "filename": "thing.jpg" "mLink": "https://google.com"}"
```
#### Request
```
{
    'mContent':'testing',
    'uUsername':'userguy',
    "mBase64":"afdghjgfdsda",
    "filename": "thing.jpg",
    "mLink":"https://google.com"

}
```
#### Response
```
{
    "mStatus": "ok",
    "mMessage": "1"
}
```
### POST /key/comments
Purpose: Inserts a new comment with specific user, message id, and content; Defaults to 0 upvotes/downvotes
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/comments -X POST -d "{'cContent':'this is a comment', "uUsername": "User guy", 'mId':0, "cBase64": "afdghjgfdsda", "filename": "thing.jpg", "cLink": "https://google.com"}"
```
#### Request
```
{
    'cContent':'this is a comment', 
    "uUsername": "User guy",
    'mId':0,
    "cBase64": "afdghjgfdsda",
    "filename": "thing.jpg",
    "cLink": "https://google.com"
}
```
#### Response
```
{
    "mStatus": "ok",
    "mMessage": "1"
}
```
### POST /users
Purpose: Inserts new user by posting idtoken from oauth; Returns key that will be used in all routes
```
curl -s https://team-snems.dokku.cse.lehigh.edu/users -X POST -d "{'idtoken':1}"
```
#### Request
```
{
    'idtoken':1
}
```
#### Response
```
{
    "mStatus": "ok",
    "mMessage": "key value"
}
```
### POST /key/users/username/id
Purpose: Adds user's vote to a post (vote == true is upvote was pressed, vote == false is downvote was pressed); id is the mId for the post that was voted on
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/users/userguy/1 -X POST -d "{'vote':true}"
```
#### Request
```
{
    "vote":true
}
```
#### Response
```
{
    "mStatus": "ok",
    "mMessage": "1"
}
```
### PUT /messages/id ** NOT USED **
Purpose: Updates a post to specific content; Maintains same number of likes
```
curl -s https://team-snems.dokku.cse.lehigh.edu/messages/0 -X PUT -d "{'mContent':'testing put'}"
```
#### Request
```
{
    'mContent':'testing',
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData":1
}
```
### PUT /key/users/username
Purpose: Updates a profile
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/users/username -X PUT -d "{'uGI': 'guy', 'uSO': 'straight', 'uNote': 'werstdhfgjklgfsdatmljlkugyftdredsawAsdrsgftghjfdsbbbbbbbbbbbbb'}"
```
#### Request
```
{
    "uGI": "guy",
    "uSO": "N/A",
    "uNote": "werstdhfgjklgfsdatmljlkugyftdredsawAsdrsgftghjfdsbbbbbbbbbbbbb",
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData":1
}
```

### PUT /key/comments/id
Purpose: Updates a comment to specific content; Maintains same number of upvotes/downvotes
```
curl -s https://team-snems.dokku.cse.lehigh.edu/1/comments/id -X PUT -d "{'mContent':'this is an edited comment'}"
```
#### Request
```
{
    'mContent':'this is an edited comment'
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData":1
}
```

### DELETE /messages/id ** NOT USED **
Purpose: Deletes a post using its id
```
curl -s https://team-snems.dokku.cse.lehigh.edu/messages/0 -X DELETE -d "{'token':1}"
```
#### Request
```
{
    'token':1
}
```
#### Response
```
{
    "mStatus":"ok"
}
```

## Entity Relationship Diagram

* [ER Diagram](Phase-3/er-diagram.png)

## Description of Tests

### Backend Tests

1. We want to test that our backend can create posts. This can be done by running `curl -s http://localhost:4567/messages -X POST -d "{'mContent':'testing', "uUsername": "User guy", 'token':1}"` to create a new post, then running a select query on the messages table in elephantsql and seeing that a new post by "User guy" has appeared with content 'testing' and no upvotes and downvotes.
2. We want to test that our backend can comment on posts. This can be done by running `curl -s http://localhost:4567/comments -X POST -d "{'cContent':'this is a comment', "uUsername": "User guy", 'mId':0, 'token':1}"` to create a comment, then running a select query on the comments table in elephantsql and seeing that a new comment by "User guy" related to the post with mId of 0 has appeared with content 'this is a comment' (editing a comment can be checked with `curl -s http://localhost:4567/comments/id -X PUT -d "{'cContent':'this is an edited comment', 'token':1}"`).
3. We want to test that our backend can upvote/downvote and remove upvotes/downvotes. This can be done by running `curl -s http://localhost:4567/messages/0/likes -X PUT -d "{'mMode':'upvote', 'token':1}"` and checking that the table has incremented upvotes by one in elephantsql (testing for adding downvotes and removing upvotes/downvotes can be done with similar calls with different mModes).
4. We want to check that our backend can get all of our posts and their comments. This can be done by running `curl -s http://localhost:4567/messages -X GET -d "{'token':1}"` and checking the output in the terminal, or checking dokku or the localhost that the get request bring the right data to the messages page.
5. We want to check that out backend can create users. This can be done by running `curl -s http://localhost:4567/users/username/edit -X PUT -d "{'uUsername':'userguy', 'uEmail':'userguy@lehigh.edu', 'uGI': 'guy', 'uSO': 'straight', 'uNote': 'werstdhfgjklgfsdatmljlkugyftdredsawAsdrsgftghjfds', 'token':1}"` and checking our tables in the database to see if a new user exists (editing a user can be checked with `curl -s http://localhost:4567/users/username/edit -X PUT -d "{'uUsername':'userguy', 'uEmail':'userguy@lehigh.edu', 'uGI': 'guy', 'uSO': 'straight', 'uNote': 'werstdhfgjklgfsdatmljlkugyftdredsawAsdrsgftghjfdsbbbbbbbbbbbbb', 'token':1}"`).
6. We want to see if our database can create and store Oauth tokens. This can be done by running `curl -s http://localhost:4567/oauth2 -X POST -d "{'token':1}"` and checking the elephantsql database for the new token (get requests should also allow our backend to display oauth tokens).
7. We want to test that our backend can store the various user session keys in the cache. We can check this by creating a new user (see test #5) and checking if the cache becomes updated with the new user.  
8. We want to confirm that our cache gets updated with every image submitted by a user. This can be checked simply by allowing a user to upload an image and then checking if it gets placed into the app Google Drive. 

### Admin Tests

1. We want to be able to create and drop the tables in the database, which can be tested with the `T` and `D` actions, respectively, and checking elephant sql for whether or not the tables are there
2. We want to be able to create messages, comments, and users in the database, which can be tested by running their respective commands and checking elephant sql for new tuples
3. We want to be able to prepopulate data into our tables, which can be done by running a prepopulate function and checking data has been added to elephant sql
4. We want to be able to validate/invalidate users and posts, which can be done by running the validate/invalidate function in the admin app and checking the changes appear in the database (can also be checked by trying to sign in on web/mobile and seeing if invalid posts appear)
5. We want to pull up all of the files used by our app, and be able to delete ones that take up too much space, are old, and have low engagement, which we can test by running our admin app to show the images, deleting them, and checking are data storage to see if they have been removed.

### Web Tests

1. We want to test that posting creates new posts with an autogenerated id, 0 likes, and a given message. These can be tested by creating a post and checking the database to see if the changes had made effect. It can also be checked by reopening the webpage to see if the new post has been created
2. We want to test that upvoting a post will increment its upvotes counter by 1. This can be tested by checking the database or refreshing the webpage.
3. We want to test that downvoting a post will increment its downvotes counter by 1. This can be tested by checking the database or refreshing the webpage.
4. We want to test that our webpage allows us to get all the posts upon opening the page. The only reasonable way to test this is to run the webpage and see if all the posts are there.
5. We want to test that creating a new post immediately adds it to the screen, and upvoting/downvoting a post immediately increments the correct counter for that post (and any other interaction based features). This can be tested by performing these actions on the webpage and checking if they do indeed do what they are supposed to do.
6. We want to test that comments can be added to a post. This can be tested by adding a comment using the menu and checking the database or refreshing the webpage.
7. We want to test that a user can edit their own comments. This can be tested by editing a comment using the menu and checking the database or refresing the webpage. We would also have to check that another user cannot edit other user's comments.
8. We want to test that a user can view other profiles. This can be tested by pressing a user on a post and opening the view profile page.
9. We want to test that a user can view/edit their own profile. This can be tested by pressing the profile button and opening the view/edit profile page.
10. We want to test that a user can attach an image to a new post/comment. This can be tested by pressing the attach image button and seeing if the image can be seen from the app.
11. We want to test that the images are stored in the cache to maximize performance. This can be tested by checking if the cache is not null after a user adds an image.
12. We want to test that a user can take a new picture to attach to a new post. This can be tested by pressing the attach an image button and seeing if the camera app opens.
13. We want to test that the app can access a user's image gallery. This can be tested by pressing the attach an image button and seeing if the gallery opens.

### Mobile Tests

1. We want to test that posting creates new posts with an autogenerated id, 0 likes, and a given message. These can be tested by creating a post and checking the database to see if the changes had made effect. It can also be checked by reopening the app to see if the new post has been created
2. We want to test that upvoting a post will increment its upvotes counter by 1. This can be tested by checking the database or refreshing the app.
3. We want to test that downvoting a post will increment its downvotes counter by 1. This can be tested by checking the database or refreshing the app.
4. We want to test that our mobile allows us to get all the posts upon opening the app. The only reasonable way to test this is to run the app and see if all the posts are there.
5. We want to test that creating a new post immediately adds it to the screen, and upvoting/downvoting a post immediately increments the correct counter for that post (and any other interaction based features). This can be tested by performing these actions on the app and checking if they do indeed do what they are supposed to do.
6. We want to test that comments can be added to a post. This can be tested by adding a comment using the menu and checking the database or refreshing the app.
7. We want to test that a user can edit their own comments. This can be tested by editing a comment using the menu and checking the database or refresing the app. We would also have to check that another user cannot edit other user's comments.
8. We want to test that a user can view other profiles. This can be tested by pressing a user on a post and opening the view profile bottom.
9. We want to test that a user can view/edit their own profile. This can be tested by pressing the profile button and opening the view/edit profile bottom.
10. We want to test that a user can attach an image to a new post/comment. This can be tested by pressing the attach image button and seeing if the image can be seen from the app.
11. We want to test that the images are stored in the cache to maximize performance. This can be tested by checking if the cache is not null after a user adds an image.
12. We want to test that a user can take a new picture to attach to a new post. This can be tested by pressing the attach an image button and seeing if the camera app opens.
13. We want to test that the app can access a user's image gallery. This can be tested by pressing the attach an image button and seeing if the gallery opens.

### Backlog

1. Add IF NOT EXISTS check in the table creation SQL
2. Add more unit tests
3. Order the database posts by ID number
4. Make mobile/web UI look better
5. Implement Mobile upvote/downvote functionality
6. Implement Mobile Profile "sexual orientation" and "gender idenitity"
7. Implement Mobile Editing Comments
8. Implement Mobile Seeing Others' Profile