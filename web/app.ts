// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;

// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var newEntryForm: NewEntryForm;

// This constant indicates the path to our back-end server (change to your own)
const backendUrl = "https://team-snems.dokku.cse.lehigh.edu";

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 * @class NewEntryForm
 */
class NewEntryForm {
    key: string;
    username: string;
    private selectedFile: File | null = null;

    /**
     * Creates an instance of NewEntryForm.
     * @param {string} id The sessionkey given by backend
     * @param {string} user The authenticated user's username
     * @memberof NewEntryForm
     */
    constructor(id: string, user: string) {
        this.key = id;
        this.username = user;
        document.getElementById("addCancel")?.addEventListener("click", (e) => {newEntryForm.clearForm();});
        document.getElementById("addButton")?.addEventListener("click", (e) => {newEntryForm.submitForm();});
        document.getElementById("showFormButton")?.addEventListener("click", (e) => {
            (<HTMLElement>document.getElementById("addElement")).style.display = "block";
            (<HTMLElement>document.getElementById("showElements")).style.display = "none";
            (<HTMLElement>document.getElementById("profileElement")).style.display = "none";
            (<HTMLElement>document.getElementById("backButton")).style.display = "inline";
            (<HTMLElement>document.getElementById("viewProfile")).style.display = "none";
            (<HTMLElement>document.getElementById("pageName")).innerHTML = "New Message";
        });
            // Add event listener for the upload button
                document.getElementById('imageUploadButton')?.addEventListener('click', (e) => {
                e.preventDefault();
                document.getElementById('imageUpload')?.click();
            });

            // Add change event listener for the file input
            document.getElementById('imageUpload')?.addEventListener('change', (e) => {
                const input = e.target as HTMLInputElement;
                if (input.files && input.files[0]) {
                    this.selectedFile = input.files[0];
                }
            });
        }



    /**
     * Clear the form's input fields
     * @memberof NewEntryForm
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("newPost")).value = "";

        // reset the UI
        (<HTMLElement>document.getElementById("addElement")).style.display = "none";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
        (<HTMLElement>document.getElementById("profileElement")).style.display = "none";
        (<HTMLElement>document.getElementById("backButton")).style.display = "none";
        (<HTMLElement>document.getElementById("viewProfile")).style.display = "none";
        (<HTMLElement>document.getElementById("pageName")).innerHTML = "Home";
    }
    
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call. 
     * @memberof NewEntryForm
     */
    submitForm() {
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let post = "" + (<HTMLInputElement>document.getElementById("newPost")).value;
        if (post === "") {
            window.alert("Error: post is not valid");
            return;
        }

        var fileconvert = "";
        let inputFile = (<HTMLInputElement>document.getElementById("input-file"));
        if (inputFile != null && inputFile.files != null){
            const file = inputFile.files[0];
            const reader = new FileReader();

            reader.addEventListener("load", () => {
                console.log(reader.result);
                fileconvert += reader.result;

            }); 

           // reader.readAsDataURL(file);

           // var fileConvert = btoa(inputFile.files[0]);

        }

        if (this.selectedFile) {
            const formData = new FormData();
            formData.append('image', this.selectedFile);
    
            // Add code here to upload formData to your server
            // This will depend on how your backend expects to receive the file
        }

        
        
        

        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/${this.key}/messages`, {
                method: 'POST',
                body: JSON.stringify({
                    mContent: post,
                    uUsername: this.username,
                    mBase64: fileconvert,
                   // mBase64: '',
                    filename: 'test'

                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                newEntryForm.onSubmitResponse(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a result.
     * @private
     * @param {*} data The object returned by the server
     * @memberof NewEntryForm
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            newEntryForm.clearForm();
            mainList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
} // end class NewEntryForm

var profilePage: ProfilePage;

/**
 *  ProfilePage encapsulates all of the code for the view/edit profile pages
 * @class ProfilePage
 */
class ProfilePage {
    key: string;
    username: string;

    /**
     * Creates an instance of ProfilePage.
     * @param {string} id The sessionkey given by backend
     * @param {string} user The authenticated user's username
     * @memberof ProfilePage
     */
    constructor(id: string, user: string) {
        this.key = id;
        this.username = user;
        document.getElementById("backButton")?.addEventListener("click", (e) => {profilePage.backToHome();});
        document.getElementById("profileButton")?.addEventListener("click", (e) => {profilePage.getOwnProfile();});
        document.getElementById("saveProfile")?.addEventListener("click", (e) => {profilePage.updateProfile();});
    }


    /**
     * Function called when back button pressed. Opens main page.
     * @memberof ProfilePage
     */
    backToHome() {
        (<HTMLElement>document.getElementById("addElement")).style.display = "none";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
        (<HTMLElement>document.getElementById("profileElement")).style.display = "none";
        (<HTMLElement>document.getElementById("backButton")).style.display = "none";
        (<HTMLElement>document.getElementById("viewProfile")).style.display = "none";
        (<HTMLElement>document.getElementById("pageName")).innerHTML = "Home";
    }


    /**
     * Function called when username of poster/comments is pressed. Opens view profile page.
     * @param {string} user The username for the profile to get
     * @memberof ProfilePage
     */
    getProfile(user: string) {
        const doAjax = async () => {
            await fetch(`${backendUrl}/${mainList.key}/users/${user}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                (<HTMLInputElement>document.getElementById("viewUsername")).innerHTML = "@" + data.mData.uUsername;
                (<HTMLInputElement>document.getElementById("viewEmail")).innerHTML = "Email: " + data.mData.uEmail;
                (<HTMLInputElement>document.getElementById("viewNote")).innerHTML = "Note: " + data.mData.uNote;
    
                (<HTMLElement>document.getElementById("addElement")).style.display = "none";
                (<HTMLElement>document.getElementById("showElements")).style.display = "none";
                (<HTMLElement>document.getElementById("profileElement")).style.display = "none";
                (<HTMLElement>document.getElementById("backButton")).style.display = "inline";
                (<HTMLElement>document.getElementById("viewProfile")).style.display = "block";
                (<HTMLElement>document.getElementById("pageName")).innerHTML = "View Profile";
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }


    /**
     * Function called when profile button is pressed. Opens view/edit own profile page.
     * @memberof ProfilePage
     */
    getOwnProfile() {
        const doAjax = async () => {
            await fetch(`${backendUrl}/${this.key}/users/${this.username}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                (<HTMLElement>document.getElementById("username")).innerHTML = "@" + data.mData.uUsername;
                (<HTMLInputElement>document.getElementById("editUsername")).value = data.mData.uUsername;
                (<HTMLElement>document.getElementById("email")).innerHTML = "Email: " + data.mData.uEmail;
                (<HTMLInputElement>document.getElementById("editEmail")).value = data.mData.uEmail;
                (<HTMLElement>document.getElementById("so")).innerHTML = "Sexual Orientation: " + data.mData.uSO;
                (<HTMLInputElement>document.getElementById("editSO")).value = data.mData.uSO;
                (<HTMLElement>document.getElementById("gi")).innerHTML = "Gender Identity: " + data.mData.uGI;
                (<HTMLInputElement>document.getElementById("editGI")).value = data.mData.uGI;
                (<HTMLElement>document.getElementById("note")).innerHTML = "Note: " + data.mData.uNote;
                (<HTMLElement>document.getElementById("editNote")).innerHTML = data.mData.uNote;

                (<HTMLElement>document.getElementById("addElement")).style.display = "none";
                (<HTMLElement>document.getElementById("showElements")).style.display = "none";
                (<HTMLElement>document.getElementById("profileElement")).style.display = "block";
                (<HTMLElement>document.getElementById("backButton")).style.display = "inline";
                (<HTMLElement>document.getElementById("viewProfile")).style.display = "none";
                (<HTMLElement>document.getElementById("pageName")).innerHTML = "Profile";
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    

    /**
     * Function called when save profile edits button is pressed. Sends updates to backend and refreshed profile page.
     * @memberof ProfilePage
     */
    updateProfile() {
        const doAjax = async () => {
            fetch(`${backendUrl}/${this.key}/users/${this.username}`, {
                method: 'PUT',
                body: JSON.stringify({
                    uGI: (<HTMLInputElement>document.getElementById("editGI")).value,
                    uSO: (<HTMLInputElement>document.getElementById("editSO")).value,
                    uNote: (<HTMLInputElement>document.getElementById("editNote")).value
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                this.getOwnProfile();
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
} // end class ProfilePage

// a global for the main ElementList of the program.  See newEntryForm for 
// explanation
var mainList: ElementList;

/**
 * ElementList provides a way of seeing all of the data stored on the server.
 * @class ElementList
 */
class ElementList {
    key: string;
    username: string;
    userVotes: any;

    /**
     * Creates an instance of ElementList.
     * @param {string} id The sessionkey given by backend
     * @param {string} user The authenticated user's username
     * @memberof ElementList
     */
    constructor(id: string, user: string) {
        this.key = id;
        this.username = user;
        this.userVotes = null;
        document.getElementById("saveComment")?.addEventListener("click", (e) => {this.updateComment((<HTMLElement>document.getElementById("saveComment")).dataset.commentid);});
    }

    /**
     * Gets the authenticated user's initial votes
     * @param {String} username The username of the authenticated user
     * @memberof ElementList
     */
    initUserVotes(username: String) {
        const doAjax = async () => {
            await fetch(`${backendUrl}/${this.key}/users/${username}/votes`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                this.userVotes = data.mData;
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
        
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * Refresh is the public method for updating ElementList
     * @memberof ElementList
     */
    refresh() {
        // Issue an AJAX GET and then pass the result to update(). 
        const doAjax = async () => {
            await fetch(`${backendUrl}/${this.key}/messages`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                this.update(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * Updates the webpage when data is changed to reflect those changes to the user
     * @private
     * @param {*} data The data given by GET /messages
     * @memberof ElementList
     */
    private update(data: any) {
        let elem_messageList = document.getElementById("messageList");

        if(elem_messageList !== null) {
            elem_messageList.innerHTML = "";

            let fragment = document.createDocumentFragment();
            let table = document.createElement('div');
            table.classList.add('row');

            for (let i = 0; i < data.mData.length; i++) {

                let cardContainer = document.createElement('div');
                cardContainer.id = "idea" + data.mData[i].mId;
                cardContainer.classList.add("col-12");
                cardContainer.classList.add("d-flex");
                cardContainer.classList.add('align-items-center');
                cardContainer.classList.add('justify-content-center');
                cardContainer.classList.add("my-3");

                let card = document.createElement('div');
                card.classList.add("card");
                card.classList.add("bg-dark");
                card.classList.add("text-white");
                card.style.maxWidth = "600px";

                let row = document.createElement('div');
                row.classList.add("row");
                row.classList.add("g-0");

                let imgContainer = document.createElement("div");
                imgContainer.classList.add("d-flex");
                imgContainer.classList.add("align-items-center");
                imgContainer.classList.add("justify-content-center");
                imgContainer.classList.add("col-4");

                let img = document.createElement("img");
                img.src = "img/default-profile.png";
                img.classList.add("img-fluid");
                img.classList.add("rounded-start");
                imgContainer.appendChild(img);

                let contentContainer = document.createElement("div");
                contentContainer.classList.add("col-8");

                let cardBody = document.createElement('div');
                cardBody.classList.add("d-flex");
                cardBody.classList.add("flex-column");
                cardBody.classList.add("text-center");
                cardBody.classList.add("card-body");
                cardBody.classList.add("h-100");

                let user = document.createElement("h4");
                user.classList.add("card-title");
                user.classList.add("fw-bold");

                let userLink = document.createElement("div");
                userLink.style.cursor = "pointer";
                userLink.id = "username" + data.mData[i].mId;
                userLink.innerHTML = "@" + data.mData[i].uUsername;
                userLink.onclick = function() {
                    profilePage.getProfile(data.mData[i].uUsername);
                };

                user.appendChild(userLink);
                cardBody.appendChild(user);

                let content = document.createElement('p');
                content.classList.add("card-text");
                content.innerHTML = data.mData[i].mContent;
                cardBody.appendChild(content);

               /***  let postImage = document.createElement("img");
                postImage.src = data.mData[i].mImageUrl; // Use the URL from your data
                postImage.classList.add("img-fluid"); // Add any necessary classes
                postImage.alt = "Post Image"; // Set an alt text for accessibility */

                let buttons = document.createElement("div");
                buttons.classList.add("mt-auto");
                buttons.classList.add("d-flex");
                buttons.classList.add("justify-content-between");
                buttons.classList.add("fs-5");

                let vote = this.isVoted(data.mData[i].mId)

                let likes = document.createElement("div");
                let likeButton = document.createElement("a");
                likeButton.onclick = function() {
                    mainList.clickVote(data.mData[i].mId, "upvote");
                };
                let thumbsUp = document.createElement("i");
                if(vote == null || vote.uVote != true) {
                    thumbsUp.classList.add("fa-regular");       // make fa-regular when not upvoted
                }
                else {
                    thumbsUp.classList.add("fa-solid");     // make fa-solid when upvoted
                }
                thumbsUp.classList.add("fa-thumbs-up");
                thumbsUp.classList.add("px-2");
                thumbsUp.style.color = "#ffffff;";
                likeButton.appendChild(thumbsUp);

                let numLikes = document.createElement("span");
                numLikes.innerHTML = data.mData[i].mUpvotes;

                likes.appendChild(likeButton);
                likes.appendChild(numLikes);

                let dislikes = document.createElement("div");
                let dislikeButton = document.createElement("a");
                dislikeButton.onclick = function() {
                    mainList.clickVote(data.mData[i].mId, "downvote");
                };
                let thumbsDown = document.createElement("i");
                if(vote == null || vote.uVote != false) {
                    thumbsDown.classList.add("fa-regular");         // make fa-regular when not downvoted
                }
                else {
                    thumbsDown.classList.add("fa-solid");       // make fa-solid when downvoted
                }
                thumbsDown.classList.add("fa-thumbs-down");
                thumbsDown.classList.add("px-2");
                thumbsDown.style.color = "#ffffff";
                dislikeButton.appendChild(thumbsDown);

                let numDislikes = document.createElement("span");
                numDislikes.innerHTML = data.mData[i].mDownvotes;

                dislikes.appendChild(dislikeButton);
                dislikes.appendChild(numDislikes);

                buttons.appendChild(likes);
                buttons.appendChild(dislikes);
                cardBody.appendChild(buttons);

                contentContainer.appendChild(cardBody);
                
                let cardFooter = document.createElement("div");
                cardFooter.classList.add("card-footer");

                let commentList = document.createElement("div");
                commentList.classList.add("list-group");
                commentList.classList.add("list-group-flush");
                commentList.classList.add("text-white");
                commentList.id = "commentList" + data.mData[i].mId;

                let commentButton = `<a class='list-group-item list-group-item-action d-flex collapse-animation bg-dark text-white fs-5 fw-bold' data-bs-toggle='collapse' href='#comments${data.mData[i].mId}' onclick='this.childNodes[1].classList.toggle("collapse-down");'>Comments<i class='fa-solid fa-chevron-right ms-auto align-self-center collapse-right'></i></a>`;
                let commentContainer = `<div id='comments${data.mData[i].mId}' class='list-group list-group-flush collapse'>`;
                let newComment = `<a class='list-group-item list-group-item-action bg-dark text-white d-flex row pe-0'><textarea id="commentContent${data.mData[i].mId}" placeholder='Add a new comment here' class='ms-0' style='width: 450px; height: 30px;'></textarea><button id='newComment${data.mData[i].mId}' class='btn btn-secondary ms-4 rounded-circle ms-auto' onclick="mainList.postComment(${data.mData[i].mId})" style="width: 45px; height: 30px;"><i class='fa-solid fa-check' style='color: #ffffff;'></i></button></a>`;
                let commentClose = "</div>";
                commentList.innerHTML = commentButton + commentContainer + newComment + commentClose;

                cardFooter.appendChild(commentList);

                row.appendChild(imgContainer);
                row.appendChild(contentContainer);
                row.appendChild(cardFooter);
                card.appendChild(row);
                cardContainer.appendChild(card);
                table.appendChild(cardContainer);
            }

            fragment.appendChild(table);
            elem_messageList.appendChild(fragment);

            for (let i = 0; i < data.mData.length; i++) {
                this.addComments(data.mData[i].mComments, data.mData[i].mId);
            }
        }
    }


    /**
     * Creates list group of comments for the specific post
     * @private
     * @param {*} commentList The list of comments for one post given by the JSON from GET /messages
     * @param {*} id The id of the post that the comments are on
     * @memberof ElementList
     */
    private addComments(commentList: any, id: any) {
        let comments = "";
        for (let i = 0; i < commentList.length; i++) {
            if(commentList[i].uUsername == this.username) {
                comments += `<a class='list-group-item list-group-item-action bg-dark text-white d-flex row pe-0'><div class="col-10"><div onclick="profilePage.getProfile('${commentList[i].uUsername}')" style="cursor: pointer;" class="d-inline-block">@${commentList[i].uUsername}:&nbsp;</div><div id='comment${commentList[i].cId}' class="d-inline-block">${commentList[i].cContent}</div></div><button id='editComment${commentList[i].cId}' class='btn btn-secondary ms-4 rounded-circle ms-auto col-2' style="width: 45px; height: 30px;" onclick="mainList.editComment(${commentList[i].cId}, '${commentList[i].cContent}')" data-bs-toggle="modal" data-bs-target="#editCommentForm"><i class='fa-solid fa-pen' style='color: #ffffff;'></i></button></a>`;
            }
            else {
                comments += `<a class='list-group-item list-group-item-action bg-dark text-white d-flex row pe-0'><div class="col-10"><div onclick="profilePage.getProfile('${commentList[i].uUsername}')" style="cursor: pointer;" class="d-inline-block">@${commentList[i].uUsername}:&nbsp;</div><div id='comment${commentList[i].cId}' class="d-inline-block">${commentList[i].cContent}</div></div></a>`;
            }
        }
        (<HTMLInputElement>document.getElementById("comments" + id)).innerHTML += comments;
    }
    

    /**
     * Function called when save new comment button pressed. Inserts new comment using POST.
     * @private
     * @param {*} id The id for the post to add the new comment to
     * @memberof ElementList
     */
    private postComment(id: any) {
        let post = "" + (<HTMLInputElement>document.getElementById("commentContent" + id)).value;
        if (post === "") {
            window.alert("Error: comment is not valid");
            return;
        }
        const doAjax = async () => {
            await fetch(`${backendUrl}/${this.key}/comments`, {
                method: 'POST',
                body: JSON.stringify({
                    cContent: post,
                    uUsername: this.username,
                    mId: id
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                this.refresh();
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }


    /**
     * Function called when save edited comment button pressed. Updates comment using PUT.
     * @param {*} cId The id of the comment to update
     * @memberof ElementList
     */
    updateComment(cId: any) {
        let comment = "" + (<HTMLInputElement>document.getElementById("editComment")).value;
        if (comment === "") {
            window.alert("Error: comment is not valid");
            return;
        }
        const doAjax = async () => {
            fetch(`${backendUrl}/${this.key}/comments/${cId}`, {
                method: 'PUT',
                body: JSON.stringify({
                    mContent: comment
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                (<HTMLElement>document.getElementById("comment" + cId)).innerHTML = comment;
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    

    /**
     * Function called when edit comment button pressed. Autofills edit comment form with current data.
     * @private
     * @param {*} id The id of the comment to be edited
     * @memberof ElementList
     *
     */

    private selectedFile: File | null = null;
    private editComment(id: any) {
        (<HTMLInputElement>document.getElementById("editComment")).innerHTML = (<HTMLInputElement>document.getElementById("comment" + id)).innerHTML;
        (<HTMLInputElement>document.getElementById("saveComment")).dataset.commentid = id;

                // Add event listener for the upload button
                document.getElementById('editCommentImageUploadButton')?.addEventListener('click', (e) => {
                    e.preventDefault();
                    document.getElementById('editCommentImageUpload')?.click();
                });
        
                // Add change event listener for the file input
                document.getElementById('editCommentImageUpload')?.addEventListener('change', (e) => {
                    const input = e.target as HTMLInputElement;
                    if (input.files && input.files[0]) {
                        this.selectedFile = input.files[0];
                    }
                });
    }

    /**
     * Function called when upvote or downvote button is pressed. Inserts vote using POST.
     * @private
     * @param {*} postID The id of the post to vote on
     * @param {String} button The type of button pressed (can be "upvote" or "downvote")
     * @memberof ElementList
     */
    private clickVote(postID: any, button: String) {

        // v is false for downvote, v is true for upvote
        let v = false;
        if(button == "upvote") {
            v = true;
        }

        const doAjax = async () => {
            fetch(`${backendUrl}/${this.key}/users/${this.username}/${postID}`, {
                method: 'POST',
                body: JSON.stringify({
                    vote: v
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                this.getUserVotes();
                this.refresh();
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }


    /**
     * Gets the authenticated user's votes
     * @private
     * @memberof ElementList
     */
    private getUserVotes() {
        const doAjax = async () => {
            await fetch(`${backendUrl}/${this.key}/users/${this.username}/votes`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                this.userVotes = data.mData;
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    

    /**
     * Determines if the post has been voted on by the authenticated user
     * @private
     * @param {*} id The id of the post to check the authenticated user's votes on
     * @return the vote JSON if the vote exists for the user, else return null
     * @memberof ElementList
     */
    private isVoted(id: any) {
        if(this.userVotes) {
            for(let i = 0; i < this.userVotes.length; i++) {
                if(this.userVotes[i].mId == id) {
                    return this.userVotes[i];
                }
            }
        }
        return null;
    }
} // end class ElementList

// Run some configuration code when the web page loads
document.addEventListener('DOMContentLoaded', () => {
    (<HTMLElement>document.getElementById("loginItems")).style.display = "block";
    (<HTMLElement>document.getElementById("authenticatedItems")).style.display = "none";
}, false);

// Function called on google sign in button press
function handleCredentialResponse(response: any) {
    const responsePayload = decodeJwtResponse(response.credential);
    let username = responsePayload.email.split("@")[0];
    postTokenID(response.credential, username)
}

// Decodes the google sign in payload
function decodeJwtResponse(data: any) {
    var tokens = data.split(".");
    return JSON.parse(atob(tokens[1]));
}

// Posts token to backend. Gets the sessionkey and initializes the authenticated screen.
function postTokenID(tokenid: string, username: string) {
    const doAjax = async () => {
        await fetch(`${backendUrl}/users`, {
            method: 'POST',
            body: JSON.stringify({
                idtoken: tokenid
            }),
            headers: {
                'Content-type': 'application/json; charset=UTF-8'
            }
        }).then( (response) => {
            // If we get an "ok" message, return the json
            if (response.ok) {
                return Promise.resolve( response.json() );
            }
            // Otherwise, handle server errors with a detailed popup message
            else{
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            return Promise.reject(response);
        }).then( (data) => {
            if(data.mStatus != "error"){
                setHomeUI(data.mMessage, username);
            }
            else {
                window.alert("Invalid user");
            }
        }).catch( (error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error");
        });
    }

    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}

// Sets the authenticated screen
function setHomeUI(key: string, username: string) {
    // Create the object that controls the "New Entry" form
    newEntryForm = new NewEntryForm(key, username);

    // Create the object that controls the profile pages
    profilePage = new ProfilePage(key, username);

    // Create the object for the main data list, and populate it with data from the server
    mainList = new ElementList(key, username);
    mainList.initUserVotes(username);

    mainList.refresh();

    // set up initial UI state
    (<HTMLElement>document.getElementById("loginItems")).style.display = "none";
    (<HTMLElement>document.getElementById("authenticatedItems")).style.display = "block";
    (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    (<HTMLElement>document.getElementById("profileElement")).style.display = "none";
    (<HTMLElement>document.getElementById("backButton")).style.display = "none";
    (<HTMLElement>document.getElementById("viewProfile")).style.display = "none";
    (<HTMLElement>document.getElementById("pageName")).innerHTML = "Home";
}

