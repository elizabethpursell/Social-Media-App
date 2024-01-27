# CSE 216 - Software Engineering 
This is the root README for team 13.

## Details
* Semester: Fall 2023
* Student names: 
    * Elizabeth Pursell (Mobile)
    * Steven McPhillimey (Admin)
    * Nix Huang (PM)
    * Subbu Meyyappan (FrontEnd)
    * Matthew Castillo (Backend)
* Student emails: 
    * emp520@lehigh.edu
    * slm526@lehigh.edu
    * xih225@lehigh.edu
    * sum225@lehigh.edu
    * mtc325@lehigh.edu
* Team 13 (Team SNEMS)
* Bitbucket Repository:
    * On the web: <https://bitbucket.org/slm526/cse216-2023fa-snems/src/master/>
    * `git clone git@bitbucket.org:slm526/cse216-2023fa-snems.git`
    * `git clone https://mtc325@bitbucket.org/slm526/cse216-2023fa-snems.git`
* Trello Board: <https://trello.com/b/KWGwAQ2i/cse216-2023fa-snems>
* ElephantSQL: <postgres://vzptwcty:password@berry.db.elephantsql.com/vzptwcty>
* Backend URL: <http://team-snems.dokku.cse.lehigh.edu/>

## Description for Sprint 12
* Implemented images, links, and file attachments for ideas and comments
* Missing functionality includes utilizing some routes for the web frontend

## Description for Sprint 9
* The project requires integrating OAuth for authentication and enhancing features such as voting and comments on a web/mobile app, with careful attention to avoid accruing technical debt. It also includes creating user profiles with inclusive designing, coordinating roles, and ensuring testing and documentation throughout the development process.

## Documentation
### Root Docs
* [Mobile-Interface](docs/Phase-2/mobile_interface.png)
* [ER-Diagram](docs/Phase-2/er-diagram.jpg)
* [User State Machine](docs/Phase-2/user-state-machine.png)
* [Idea State Machine](docs/Phase-2/idea-state-machine.png)
* [System-Diagram](docs/Phase-2/system_diagram.png)
* [Web_Interface](docs/Phase-2/web_ui.png)
* [README.md](docs/README-phase2.md)

### Developer Docs
* Backend: 
    * [Index-All](backend/apidocs/index-all.html)

* Web:
    * [Index](web/docs/index.html)
    * [Modules](web/docs/modules.html)
    
* Admin: 
    * [AllClasses-Index](admin-cli/apidocs/allclasses-index 2.html)
    * [AllPackages-Index](admin-cli/apidocs/allpackages-index 2.html)
    * [Help-Doc](admin-cli/apidocs/help-doc 2.html)
    * [Index-All](admin-cli/apidocs/index-all 2.html)
    * [Index](admin-cli/apidocs/index 2.html)
    * [Overview-Tree](admin-cli/apidocs/overview-tree 2.html)
    
* Mobile: 
    * [Index](mobile/flutter/snems_app/doc/api/index.html)
    * [Search](mobile/flutter/snems_app/doc/api/search.html)

## Running on Dokku
1. In the git repository from the command line, execute `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'ps:start team-snems'` to start the Dokku app.
2. Run `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'config:set team-snems CORS_ENABLED=true'` to enable CORS.
3. Opening the link <http://team-snems.dokku.cse.lehigh.edu/messages> will now show all the messages in your database.
4. If you make any changes to the backend code, be sure to make them in the backend-dokku branch and run `git push dokku backend-dokku:master` to apply them to Dokku.
5. To run the frontend using the Dokku backend, go into the web directory and run `sh local-deploy.sh` to create a local server. Open the link given <http://127.0.0.1:8080/> and see the Todo app that we created. The data is now stored in a database. Stop the local server using CTRL+C
6. To stop the Dokku app, first disable CORS using `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'config:set team-snems CORS_ENABLED=false'`. Then, stop the app with `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'ps:stop team-snems'`

## Running Locally
1. In the git repository backend folder from the command line, run `mvn clean; mvn package` to package the backend into an executable jar file.
2. Run the jar file using `PORT=8998 DATABASE_URL=postgres://<your db user>:<your db password>@<your db host>/<your db user> mvn exec:java`. This will create a local backend server where you can see your database contents at the link <http://localhost:8998/messages>
3. Stop the local backend server using CTRL+C.

## Dokku Notes
* To edit the backend code, write the code in the backend branch and copy the src folder to the backend-dokku branch. In the backend folder in the backend branch, run `cp -r src/ ~/Downloads` to copy the src folder to your downloads. Then, in the backend-dokku branch root folder, run `cp -r ~/Downloads/src .` to copy it to the backend-dokku branch. Commit and push these changes to bitbucket.  Finally, push the code changes to dokku using `git push dokku backend-dokku:master`
* Every PUT command should have its own route
* To add SQL commands that change the database, add a prepared statement in Database.java

## Running the Web Frontend Locally
1. Clone this repository to your local computer using `git clone git@bitbucket.org:emp520-cse216/tutorials.git` or `git clone https://emp520@bitbucket.org/emp520-cse216/tutorials.git`
2. Go to the web folder using `cd web`
3. Deploy the frontend using `sh deploy.sh`
4. Open a new terminal
5. Go to the backend folder using `cd backend`
6. Run the local web server using `STATIC_LOCATION=`pwd`/src/main/resources/web/ mvn exec:java`
7. Open the web page at http://localhost:4567/
8. Any frontend changes can be integrated by rerunning `sh deploy.sh`

## Generating JavaDoc Artifacts Using Maven
1. Add the dependency to the build plugins in the pom.xml. See the references for more guidance. This is setup for the backend already.
2. Run `mvn clean; mvn package` from the backend or admin folder to clear the old jar and artifacts and generate the new jar.
3. Run `mvn javadoc:javadoc` to verify that all the JavaDoc comments are correct.
4. Run `mvn javadoc:jar` to generate the artifacts based on your JavaDoc comments. They will be in the target/apidocs folder. Open the HTML files to see the JavaDocs.
5. Copy the apidocs folder to the backend or admin folder. Add, commit, push this to bitbucket

**References**  
- [Plugin Overview](https://maven.apache.org/plugins/maven-javadoc-plugin/)  
- [Plugin Usage Examples](https://maven.apache.org/plugins/maven-javadoc-plugin/usage.html)

## Generating TypeDoc Artifacts
1. npm install --save-dev typedoc
2. npx typedoc (filename)

## Adding Web Frontend to Dokku
1. First, we need to get the up-to-date web code, so in the web branch, copy the web folder to your downloads. Run this command from the web branch, `cp web ~/Downloads`
2. Now, we need to put the current web code in the backend branch. From the backend branch, run this command `cp ~/Downloads/web .`
3. After that, we need to deploy the web code to our backend. Run this command from the web folder in the backend branch `sh deploy.sh`. This puts the web code in index.html in the src/.../resources/web folder.
4. Check that this is running locally by running `DATABASE_URL=<database_url> mvn package; DATABASE_URL=<database_url> mvn exec:java`. The link <localhost:4567/> should have the current frontend.
5. Next, we need to put this web-deployed backend onto dokku. Copy the current src folder to your downloads by running `cp src ~/Downloads` from the backend folder in your backend branch.
6. Then, we need to add the updated src folder to our dokku branch. From the backend-dokku branch, run this command to update the src folder `cp ~/Downloads/src .`
7. Now, push these changes to bitbucket so dokku can detect these updates.
8. Finally, run `git push dokku backend-dokku:master` to push the changes to dokku. Opening the link <https://team-snems.dokku.cse.lehigh.edu/> should now show the current web frontend.

## Running Admin Locally
1. Move into the admin-cli folder using `cd` command
2. From there, run `mvn clean; mvn package` to clean and compile the code
3. Using the proper environment variables prior to the command, run `man exec:java`
4. Run your admin actions straight from the command line

# Running Mobile Locally 
1. Connect an anroid device through VS Code. 
2. After that, open the main.dart file and use the option " Run and Debug".
3. Once the android device connection is succesful and the dart file has been debugged you could see the app being deployed on the emulator. 
4. While the app is deployed and running on the emulator and you make any changes to your file, you could see them reflect on the app by using the hot reload option rather than exiting the app and running it all over again.