import 'package:flutter/material.dart';

import 'package:my_tutorial_app/pages/login_page.dart';
import 'package:my_tutorial_app/main.dart';

class ProfilePageView extends StatelessWidget {
  ProfilePageView({super.key, this.user, this.userEmail, this.userNote});

  final user;
  final userEmail;
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
                  style: const TextStyle(color: Colors.white, fontSize: 30, fontWeight:FontWeight.bold),
                ),
                const SizedBox(height: 8),
                //Profile displaying user email
                Text(
                  'Email: $userEmail',
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
}
