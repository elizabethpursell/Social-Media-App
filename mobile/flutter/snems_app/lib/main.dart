import 'dart:async';

import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:my_tutorial_app/components/votes_button.dart';
import 'package:my_tutorial_app/pages/comment_page.dart';
import 'package:my_tutorial_app/pages/picture_page.dart';
import 'package:my_tutorial_app/pages/google_signin_api.dart';
import 'package:my_tutorial_app/pages/profile_page_edit.dart';
import 'package:my_tutorial_app/pages/profile_page_view.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_file_plus/open_file_plus.dart';

import 'dart:io';

import 'pages/login_page.dart';
import 'dart:developer' as developer;

class MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  }
}

File? tempFile = null;

// https://simondev.medium.com/use-sharedpreferences-in-flutter-effortlessly-835bba8f7418
class SharedPrefs {
  late final SharedPreferences prefs;

  Future<void> init() async {
    prefs = await SharedPreferences.getInstance();
  }

  String? getStringValue(String key) {
    return prefs.getString(key);
  }

  bool? getBoolValue(String key) {
    return prefs.getBool(key);
  }

  setStringValue(String key, String val) {
    prefs.setString(key, val);
  }

  setBoolValue(String key, bool val) {
    prefs.setBool(key, val);
  }

  bool checkKey(String key) {
    return prefs.containsKey(key);
  }

  Future<void> clearAll() async {
    await prefs.clear();
  }
}

final cache = SharedPrefs();

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  HttpOverrides.global = MyHttpOverrides();
  await cache.init();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: LoginPage(),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SNEMS',
      home: MessagePage(),
    );
  }
}

class MessagePage extends StatefulWidget {
  @override
  _MessagePageState createState() => _MessagePageState();
}

class _MessagePageState extends State<MessagePage> {
  final backendUrl = 'https://team-snems.dokku.cse.lehigh.edu/';
  List<dynamic> messages = [];
  final messageController = TextEditingController();
  final sessionKey = cache.getStringValue("sessionKey");
  final loggedInUser = cache.getStringValue("username");
  final commentController = TextEditingController();
  final linkController = TextEditingController();

  @override
  void initState() {
    super.initState();
    cacheUserVotes();
    fetchMessages();
  }

  Future<void> cacheUserVotes() async {
    final response = await http
        .get(Uri.parse('$backendUrl/$sessionKey/users/$loggedInUser/votes'));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      await cache.clearAll();
      cache.setStringValue("sessionKey", sessionKey!);
      cache.setStringValue("username", loggedInUser!);
      if (data["mData"].length > 0) {
        for (var v in data["mData"]) {
          var mId = v["mId"];
          var voteType = v["uVote"];
          cache.setBoolValue("$mId vote", voteType);
        }
      }
    }
  }

  Future<void> fetchMessages() async {
    final response =
        await http.get(Uri.parse('$backendUrl/$sessionKey/messages'));
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      setState(() {
        messages = data['mData'];
      });
    }
  }

  Future<void> addMessage(String content, String link) async {
    var b64 = "";
    var filename = "";
    if (tempFile != null) {
      filename = tempFile!.path.split('/').last;
      b64 = base64Encode(tempFile!.readAsBytesSync());
      tempFile = null;
    }
    final response = await http.post(
      Uri.parse('$backendUrl/$sessionKey/messages'),
      body: json.encode({
        'mContent': content,
        'uUsername': loggedInUser,
        "mBase64": b64,
        "filename": filename,
        "mLink": link
      }),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      fetchMessages();
    }
  }

  //Add comment
  Future<void> addComment(String content, int messageId, String link) async {
    var b64 = "";
    var filename = "";
    if (tempFile != null) {
      filename = tempFile!.path.split('/').last;
      b64 = base64Encode(tempFile!.readAsBytesSync());
      tempFile = null;
    }
    final response = await http.post(
      Uri.parse('$backendUrl/$sessionKey/comments'),
      body: json.encode({
        'mId': messageId,
        'uUsername': loggedInUser,
        'cContent': content,
        "cBase64": b64,
        "filename": filename,
        "cLink": link
      }),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      fetchMessages();
    }
  }

  //show a dialogue box for inputting comment
  void showCommentDialog(int messageId) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: const Text("Add Comment"),
              content: Column(mainAxisSize: MainAxisSize.min, children: [
                TextField(
                  controller: commentController,
                  decoration: InputDecoration(hintText: "Write a comment.."),
                ),
                TextField(
                  controller: linkController,
                  decoration: InputDecoration(hintText: "Add a link.."),
                ),
                StatefulBuilder(builder: (context, setState) {
                  return Row(
                    children: [
                      IconButton(
                          onPressed: () async {
                            await Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const GalleryAccess()),
                            );
                            setState(() {});
                          },
                          icon: const Icon(Icons.insert_photo)),
                      Text(tempFile != null
                          ? "File Selected"
                          : "No File Selected")
                    ],
                  );
                })
              ]),
              actions: [
                //cancel button
                TextButton(
                  //pop box
                  onPressed: () {
                    Navigator.pop(context);
                    //clear controller
                    commentController.clear();
                  },
                  child: Text("Cancel"),
                ),
                // save button
                TextButton(
                    onPressed: () {
                      //add comment
                      addComment(commentController.text, messageId,
                          linkController.text);
                      //pop box
                      Navigator.pop(context);
                      //clear controller
                      commentController.clear();
                      linkController.clear();
                    },
                    child: Text("Post")),
              ],
            ));
  }

  //show a dialogue box for attaching a link/image
  void showAttachmentMenu() {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: Text("Add Attachments"),
              content: Column(mainAxisSize: MainAxisSize.min, children: [
                TextField(
                  controller: linkController,
                  decoration: InputDecoration(hintText: "Add a link.."),
                ),
                StatefulBuilder(builder: (context, setState) {
                  return Row(
                    children: [
                      IconButton(
                          onPressed: () async {
                            await Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const GalleryAccess()),
                            );
                            setState(() {});
                          },
                          icon: const Icon(Icons.insert_photo)),
                      Text(tempFile != null
                          ? "File Selected"
                          : "No File Selected")
                    ],
                  );
                })
              ]),
              actions: [
                //cancel button
                TextButton(
                  //pop box
                  onPressed: () {
                    Navigator.pop(context);
                    //clear controller
                    linkController.clear();
                  },
                  child: Text("Cancel"),
                ),
                //save button
                TextButton(
                    onPressed: () {
                      //pop box
                      Navigator.pop(context);
                    },
                    child: Text("Attach")),
              ],
            ));
  }

  Future<void> vote(int id, bool voteType) async {
    final response = await http.post(
      Uri.parse('$backendUrl/$sessionKey/users/$loggedInUser/$id'),
      body: json.encode({'vote': voteType}),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      cacheUserVotes();
      fetchMessages();
    } else {
      print('Cannot load vote');
    }
  }

  Future<void> signUserOut() async {
    GoogleSignInApi.logOut();
    await cache.clearAll();
    Navigator.of(context).pushReplacement(MaterialPageRoute(
      builder: (context) => LoginPage(),
    ));
  }

  void seeAllComments(int messageID) {
    Navigator.of(context).pushReplacement(MaterialPageRoute(
      builder: (context) => CommentPage(id: messageID, user: loggedInUser!),
    ));
  }

  void seeProfile(String user) async {
    if (user == loggedInUser) {
      final response =
          await http.get(Uri.parse('$backendUrl/$sessionKey/users/$user'));
      if (response.statusCode == 200) {
        final data = json.decode(response.body)["mData"];
        Navigator.of(context).pushReplacement(MaterialPageRoute(
          builder: (context) => ProfilePage(
              user: data["uUsername"],
              userEmail: data["uEmail"],
              userGI: data["uGI"],
              userSO: data["uSO"],
              userNote: data["uNote"]),
        ));
      }
    } else {
      final response =
          await http.get(Uri.parse('$backendUrl/$sessionKey/users/$user'));
      if (response.statusCode == 200) {
        final data = json.decode(response.body)["mData"];
        Navigator.of(context).pushReplacement(MaterialPageRoute(
          builder: (context) => ProfilePageView(
              user: data["uUsername"],
              userEmail: data["uEmail"],
              userNote: data["uNote"]),
        ));
      }
    }
  }

  String getVoteStatus(int mId) {
    if (cache.checkKey("$mId vote")) {
      bool? val = cache.getBoolValue("$mId vote");
      if (val == true) {
        return "upvoted";
      } else if (val == false) {
        return "downvoted";
      } else {
        return "none";
      }
    }
    return "none";
  }

  List<PopupMenuEntry> getDropdown(message) {
    List<PopupMenuEntry> dropdown = [
      const PopupMenuItem<int>(
        value: 0,
        child: Row(children: [
          Icon(
            Icons.add_comment,
            color: Colors.grey,
          ),
          Text("Add Comment")
        ]),
      ),
      const PopupMenuItem<int>(
        value: 1,
        child: Row(children: [
          Icon(
            Icons.comment,
            color: Colors.grey,
          ),
          Text("View Comments")
        ]),
      )
    ];
    if (message["mLink"] != null && message["mLink"] != "") {
      dropdown.add(const PopupMenuItem<int>(
        value: 2,
        child: Row(children: [
          Icon(
            Icons.open_in_new,
            color: Colors.grey,
          ),
          Text("Open Link")
        ]),
      ));
    }
    if (message["mBase64"] != null && message["mBase64"] != "") {
      dropdown.add(const PopupMenuItem<int>(
        value: 3,
        child: Row(children: [
          Icon(
            Icons.file_open_rounded,
            color: Colors.grey,
          ),
          Text("Open File")
        ]),
      ));
    }
    return dropdown;
  }

  void openLink(String link) async {
    final Uri url = Uri.parse(link);
    if (!await launchUrl(url)) {
      throw Exception('Could not launch $url');
    }
  }

  void openFile(String b64, String filename) async {

    final file = await downloadFile(b64, filename);
    if (file == null) {
      return;
    }
    if (!cache.checkKey(filename)) {
      cache.setStringValue(filename, b64);
    }
    OpenFile.open(file.path);
  }

  Future<File?> downloadFile(String b64, String filename) async {
    try {
      final appStorage = await getApplicationDocumentsDirectory();
      final file = File("${appStorage.path}/$filename");
      final raf = file.openSync(mode: FileMode.write);
      final bytes = base64.decode(b64);
      raf.writeFromSync(bytes);
      await raf.close();
      return file;
    } catch (e) {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('SNEMS'),
        actions: <Widget>[
          IconButton(onPressed: signUserOut, icon: const Icon(Icons.logout)),
          IconButton(
              onPressed: () {
                seeProfile(loggedInUser!);
              },
              icon: const Icon(Icons.account_box))
        ],
        backgroundColor: Colors.purple,
      ),
      backgroundColor: Colors.white,
      body: Column(
        children: [
          Expanded(
            child: ListView.separated(
              padding: const EdgeInsets.all(8),
              itemCount: messages.length,
              itemBuilder: (context, index) {
                final message = messages[index];
                List<PopupMenuEntry> dropdown = getDropdown(message);
                return ListTile(
                  leading: ViewUserButton(
                      onTap: () => seeProfile(message["uUsername"]),
                      user: message["uUsername"]),
                  title: Text(message['mContent']),
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      //upvote button
                      VoteButton(
                          onTap: () => vote(message['mId'], true),
                          votes: message["mUpvotes"],
                          voteStatus: getVoteStatus(message["mId"]),
                          voteType: "upvote"),
                      //downvote button
                      VoteButton(
                          onTap: () => vote(message['mId'], false),
                          votes: message["mDownvotes"],
                          voteStatus: getVoteStatus(message["mId"]),
                          voteType: "downvote"),
                      PopupMenuButton(
                          child: const Icon(Icons.more_vert),
                          itemBuilder: (context) {
                            return dropdown;
                          },
                          onSelected: (value) {
                            if (value == 0) {
                              showCommentDialog(message['mId']);
                            } else if (value == 1) {
                              seeAllComments(message['mId']);
                            } else if (value == 2) {
                              openLink(message["mLink"]);
                            } else if (value == 3) {
                              //openFile("1W6aQn1wFFS4qiJA1IgK72g9L3wAMDDRK", 71);
                              openFile(
                                  message["mBase64"], message["mFilename"]);
                            }
                          }),
                    ],
                  ),
                );
              },
              separatorBuilder: (BuildContext context, int index) =>
                  const Divider(),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: messageController,
                    decoration: InputDecoration(hintText: 'Enter a message'),
                  ),
                ),
                IconButton(
                  icon: Icon(Icons.attach_file),
                  onPressed: () {
                    showAttachmentMenu();
                  },
                ),
                IconButton(
                  icon: Icon(Icons.send),
                  onPressed: () {
                    addMessage(messageController.text, linkController.text);
                    messageController.clear();
                    linkController.clear();
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
