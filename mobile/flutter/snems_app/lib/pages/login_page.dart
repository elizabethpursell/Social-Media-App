import 'package:flutter/material.dart';
import 'package:my_tutorial_app/main.dart';
import 'package:my_tutorial_app/pages/google_signin_api.dart';
import 'square_tile.dart';

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'dart:developer' as developer;

class LoginPage extends StatefulWidget {
  LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final backendUrl = 'https://team-snems.dokku.cse.lehigh.edu';

  Future googleSignIn() async {
    final googleUser = await GoogleSignInApi.login();
    final googleAuth = await googleUser!.authentication;
    final token = googleAuth.idToken;

    final email = googleUser.email;
    final user = email.split('@')[0];

    final response = await http.post(
      Uri.parse('$backendUrl/users'),
      body: json.encode({'idtoken': token}),
      headers: {'Content-Type': 'application/json'},
    );

    var key = jsonDecode(response.body)['mMessage'];

    if (response.statusCode == 200) {
      Navigator.of(context).pushReplacement(MaterialPageRoute(
        builder: (context) => const HomePage(),
      ));
    }

    cache.setStringValue('sessionKey', key);
    cache.setStringValue('username', user);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: Colors.purple[400],
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const SizedBox(height: 40),

              // logo
              const Icon(
                Icons.lock,
                size: 50,
              ),

              const SizedBox(height: 40),

              // welcome back, you've been missed!
              Text(
                'Welcome back you\'ve been missed!',
                style: TextStyle(
                  color: Colors.grey[750],
                  fontSize: 16,
                ),
              ),

              const SizedBox(height: 40),

              // google sign in buttons
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // google button
                  SquareTile(
                    onTap: googleSignIn,
                    imagePath: 'lib/images/google.png',
                  )
                ],
              ),

              const SizedBox(height: 35),
            ],
          ),
        ),
      ),
    );
  }
}
