import 'package:flutter/material.dart';

import 'package:my_tutorial_app/pages/login_page.dart';
import 'package:my_tutorial_app/main.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ProfilePage extends StatelessWidget {
  ProfilePage(
      {super.key,
      this.user,
      this.userEmail,
      this.userGI,
      this.userSO,
      this.userNote});

  final user;
  final userEmail;
  final userGI;
  final userSO;
  final userNote;

  @override
  Widget build(BuildContext context) {
    void goBacktoHomePage() {
      Navigator.of(context).pushReplacement(MaterialPageRoute(
        builder: (context) => HomePage(),
      ));
    }

    return Scaffold(
        appBar: AppBar(
          title: const Text('Profile'),
          centerTitle: true,
          leading: GestureDetector(
            onTap: () => goBacktoHomePage(),
            child: const Icon(
              Icons.arrow_back,
            ),
          ),
          actions: <Widget>[
            IconButton(
                onPressed: () {
                  editProfileMenu(context);
                },
                icon: const Icon(Icons.edit))
          ],
          backgroundColor: Colors.purple,
        ),
        body: Container(
            alignment: Alignment.center,
            color: Colors.purple,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Profile',
                  style: TextStyle(fontSize: 24),
                ),
                const SizedBox(height: 32),
                //Profile displaying user image
                CircleAvatar(
                  radius: 60,
                  backgroundColor: Colors.blueGrey,
                  child: Image.asset('lib/images/default-profile.png'),
                ),
                const SizedBox(height: 8),
                //Profile displaying user name
                Text(
                  '@$user',
                  style: const TextStyle(
                      color: Colors.white,
                      fontSize: 30,
                      fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 8),
                //Profile displaying user email
                Text(
                  'Email: $userEmail',
                  style: const TextStyle(color: Colors.white, fontSize: 20),
                ),
                const SizedBox(height: 8),
                //Profile displaying user SO
                Text(
                  'Sexual Orientation: $userSO',
                  style: const TextStyle(color: Colors.white, fontSize: 20),
                ),
                const SizedBox(height: 8),
                //Profile displaying user GI
                Text(
                  'Gender Identity: $userGI',
                  style: const TextStyle(color: Colors.white, fontSize: 20),
                ),
                const SizedBox(height: 8),
                //Profile displaying user note
                Text(
                  'Note: $userNote',
                  style: const TextStyle(color: Colors.white, fontSize: 20),
                ),
                const SizedBox(height: 8),
              ],
            )));
  }

  //show a dialogue box for editing profile
  void editProfileMenu(BuildContext context) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: Text("Edit Profile"),
              content: Column(
                children: [
                  TextField(
                    controller: soController,
                    decoration: InputDecoration(hintText: userSO),
                  ),
                  TextField(
                    controller: giController,
                    decoration: InputDecoration(hintText: userGI),
                  ),
                  TextField(
                    controller: noteController,
                    decoration: InputDecoration(hintText: userNote),
                  ),
                ]
              ),
              actions: [
                //cancel button
                TextButton(
                  //pop box
                  onPressed: () {
                    Navigator.pop(context);
                    //clear controller
                    soController.clear();
                    giController.clear();
                    noteController.clear();
                  },
                  child: Text("Cancel"),
                ),
                //save button
                TextButton(
                    onPressed: () {
                      //add comment
                      saveProfile(soController.text, giController.text, noteController.text, context);
                      //clear controller
                      soController.clear();
                      giController.clear();
                      noteController.clear();
                    },
                    child: Text("Save")),
              ],
            ));
  }

  void saveProfile(String newSO, String newGI, String newNote, BuildContext context) async {
    final sessionKey = cache.getStringValue("sessionKey");
    final response = await http.put(
      Uri.parse(
        'https://team-snems.dokku.cse.lehigh.edu/$sessionKey/users/$user'
      ),
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(<String, String>{
          'uGI': newGI,
          'uSO': newSO,
          'uNote': newNote,
        }),
    );

    if (response.statusCode == 200) {
      Navigator.pop(context);
      Navigator.of(context).pushReplacement(MaterialPageRoute(
          builder: (context) => ProfilePage(
              user: user,
              userEmail: userEmail,
              userGI: newGI,
              userSO: newSO,
              userNote: newNote,
          ),
      ));
    } else {
      throw Exception('Failed to update profile.');
    }
  }
}

final soController = TextEditingController();
final giController = TextEditingController();
final noteController = TextEditingController();

class ViewUserButton extends StatefulWidget {
  final void Function()? onTap;
  final String user;
  const ViewUserButton({super.key, required this.onTap, required this.user});

  @override
  _ViewUserButtonState createState() => _ViewUserButtonState();
}

class _ViewUserButtonState extends State<ViewUserButton> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onTap: () {
          widget.onTap!();
        },
        child: Column(
          children: [
            const Icon(
              Icons.account_circle_sharp,
              color: Colors.amber,
            ),
            Text(widget.user)
          ]
        )
    );
  }
}
