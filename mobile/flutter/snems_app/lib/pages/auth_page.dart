import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:my_tutorial_app/main.dart';
import 'package:my_tutorial_app/pages/login_page.dart';

class AuthPage extends StatelessWidget{
  const AuthPage({super.key});

  @override
  Widget build(BuildContext context){
    return Scaffold(
      resizeToAvoidBottomInset: false,
      body:StreamBuilder<User?>(
        stream:FirebaseAuth.instance.authStateChanges(),
        builder: (context, snapshot){
        //user logged in
        if(snapshot.hasData){
          return HomePage();
        }
        //user not logged in
        else{
          return LoginPage();
        }
        },
      ),
    );
  }
}