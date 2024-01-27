# Phase 1 Documentation

## User Stories

### Anonymous User Stories

1.  
    * As **an anonymous user**
    * I want to **see posts**
    * So I can **experience the thoughts of others**
2.  
    * As **an anonymous user**
    * I want to **create a post**
    * So I can **share my thoughts**
3.  
    * As **an anonymous user**
    * I want to **like a post**
    * So I can **express my agreement**
4.  
    * As **an anonymous user**
    * I want to **unlike a post**
    * So I can **express newfound disagreement or undo a stray like**

### Admin Stories

1.  
    * As **an admin**
    * I want to **create tables**
    * So I can **add a table to store messages and any future tables our app might need**
2.  
    * As **an admin**
    * I want to **delete tables**
    * So I can **remove incorrect or unneeded clutter from the database to save space**
3.  
    * As **an admin**
    * I want to **remove an entry from a table**
    * So I can **remove broken or inappropriate posts**

## Tests for User Stories
1. Manual testing is very hands-on. It requires analysts and QA engineers to be highly involved in everything from test case creation to actual test execution. 
2. Automated testing involves testers writing test scripts that automate test execution. (A test script is a set of instructions to be performed on target platforms to validate a feature or expected outcome.)

**Manual Test for User Story**
    Scenario 1: Create a post
    Scenario 2: Like a post
    Scenario 3: unlike a post

    Test case 1:
    1. Go to the home page
    2. Click create a post button
    3. Input contents
    4. Click "post" button

    Test case 2:
    1. Go to the home page
    2. See someone's post that you like
    3. Click "like" button

    Test case 3:
    1. Go to the home page
    2. See a post that you do not like
    3. Click "dislike" button

**Automated Test for User Story**
    1. Select the right test automation tool, such as Testsigma
    2. Write the scripts for each test cases from manuel test for user stories
    3. Run the script to see if each button is functioning properly.

## Drawing of System
Found at ```./system_diagram.png```

## Drawings of Web/Mobile User Interfaces
Web drawing found at ```./web_interface_drawing.jpg```
Mobile drawing found at ```./mobile_interface_drawing.jpg```

## Drawing of State Machine
Found at ```./state_diagram.png```

## Routes
### GET /messages
Purpose: Gets the ids, content, and likes for all messages
```
curl -s http://localhost:4567/messages -X GET
```
#### Response
```
{
    "mStatus":"ok",
    "mData":[
        {
            "mId":0,
            "mContent":"This is a test post",
            "mLikes":4,
            "mCreated":"Oct 7, 2023, 9:01:46 PM"
        }, {
            "mId":2,
            "mContent":"Testing 2",
            "mLikes":1,
            "mCreated":"Oct 7, 2023, 9:01:46 PM"
        }
    ]
}
```
### GET /messages/id
Purpose: Gets the id, content, likes for one post
```
curl -s http://localhost:4567/messages/0 -X GET
```
#### Response
```
{
    "mStatus":"ok",
    "mData": {
        "mId":0,
        "mContent":"This is a test post",
        "mLikes":4,
        "mCreated":"Oct 8, 2023 12:40:48 AM"
        }
}
```
### POST /messages
Purpose: Inserts a new post with specific content; Defaults to 0 likes
```
curl -s http://localhost:4567/messages -X POST -d "{'mContent':'testing'}"
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
    "mStatus": "ok",
    "mMessage": "1"
}
```
### PUT /messages/id
Purpose: Updates a post to specific content; Maintains same number of likes
```
curl -s http://localhost:4567/messages/0 -X PUT -d "{'mContent':'testing put'}"
```
#### Request
```
{
    'mContent':'testing'
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData":1
}
```

### DELETE /messages/id
Purpose: Deletes a post using its id
```
curl -s http://localhost:4567/messages/0 -X DELETE
```
#### Request
```
{
    "id":0
}
```
#### Response
```
{
    "mStatus":"ok"
}
```

### PUT /messages/id/likes
Purpose: Updates a post's number of likes based on whether it is 'liked' or 'unliked' mode
```
curl -s http://localhost:4567/messages/0/likes -X PUT -d "{'mMode':'liked'}"
```
#### Request
```
{
    'mMode':'liked'
}
```
#### Response
```
{
    "mStatus":"ok",
    "mData":1
}
```

## Entity Relationship Diagram

Found at ```./ER_diagram.png```

## Description of Tests

### Backend Tests

1. We want to test that the id, content, and number of likes of all the posts can be retrieved from the database. This can be tested by running the backend locally and looking at the localhost:4567/messages page. That page should have the necessary information for all posts in JSON format. We can also run the curl command `curl -s http://localhost:4567/messages -X GET` to get the JSON content of the page.
2. We want to test that the id, content, and number of likes of one post can be retrieved from the database. This can be tested by running the backend locally and looking at the localhost:4567/messages/id page, where id is the id number of the post. That page should have the necessary information for the post in JSON format. We can also run the curl command `curl -s http://localhost:4567/messages/id -X GET`, where id is the id number of the post, to get the JSON content of the page.
3. We want to test that a new post (with specific content) can be added to the database. This can be tested by running the backend locally and executing the curl command `curl -s http://localhost:4567/messages -X POST -d "{'mContent':'testing'}"`. When the page http://localhost:4567/messages is refreshed, the new message with 0 likes should be added to the JSON content.
4. We want to test that a post can be editted (with new content) and saved to the database. This can be tested by running the backend locally and executing the curl command `curl -s http://localhost:4567/messages/id -X PUT -d "{'mContent':'testing put'}"`, where id is the id number of the post. When the page http://localhost:4567/messages is refreshed, the post should have the updated content with the same number of likes.
5. We want to test that a post can be deleted from the database. This can be tested by running the backend locally and executing the curl command `curl -s http://localhost:4567/messages/id -X DELETE`, where id is the id number of the post. When the page http://localhost:4567/messages is refreshed, the post should have been deleted from the JSON content.
6. We want to test that a post can be liked. This can be tested by running the backend locally and executing the curl command `curl -s http://localhost:4567/messages/id/likes -X PUT -d "{'mMode':'liked'}"`, where id is the id number of the post. When the page http://localhost:4567/messages is refreshed, the post should have one more like than it had before.
6. We want to test that a post can be unliked. This can be tested by running the backend locally and executing the curl command `curl -s http://localhost:4567/messages/id/likes -X PUT -d "{'mMode':'unliked'}"`, where id is the id number of the post. When the page http://localhost:4567/messages is refreshed, the post should have one less like than it had before.

### Admin Tests

1. We want to be able to test that our java program creates tables. We can do this by running it's create table program and seeing if it generates a table in elephantsql
2. We want to be able to test that our java program drops tables. We can do this by creating a table with our program, checking that it exists in elephantsql, then deleting the table and checking that it is removed from elephantsql
3. We want to be able to test that our java program can remove entries from tables. We can do this by running it for a specific entry for a specific table and seeing that it has been removed from the table.

### Web Tests

1. We want to test that posting creates new posts with an autogenerated id, 0 likes, and a given message. These can be tested by creating a post and checking the database to see if the changes had made effect. It can also be checked by reopening the webpage to see if the new post has been created
2. We want to test that liking a post will increment its like counter by 1. This can be  tested by checking the database or refreshing the webpage.
3. We want to test that our webpage lets us get all the posts upon opening the page. THe only reasonable way to test this is to run the webpage and see if all the posts are there.
4. We want to test that creating a new post immediately adds it to the screen, and liking a post immediately incrememnts the like counter for that post (and any other interaction based features). This can be tested by performing these actions on the webpage and checking if they do indeed do what they are supposed to do.

### Mobile Tests

1. At the end of this phase, the mobile interface should be almost identical to the web interface, and their tests should therefore be the same.
