import 'package:google_sign_in/google_sign_in.dart';
//web client key
const GOOGLE_CLIENT_DEV_KEY = '1062513849505-op7q17t0b43ef7n581nbjopa0m6t4q6c.apps.googleusercontent.com';
//mobile client key
// const GOOGLE_CLIENT_DEV_KEY = '1062513849505-imm66c35rrd6pecsrv0uqr8su88nengf.apps.googleusercontent.com';
class GoogleSignInApi{
  static final _googleSignIn = GoogleSignIn(
      serverClientId: GOOGLE_CLIENT_DEV_KEY,
      // scopes: [
      //   'https://www.googleapis.com/auth/userinfo.email',
      //   'openid',
      //   'https://www.googleapis.com/auth/userinfo.profile',
      // ],
  );
  static Future<GoogleSignInAccount?> login() => _googleSignIn.signIn();  

  static Future logOut() => _googleSignIn.disconnect();
}