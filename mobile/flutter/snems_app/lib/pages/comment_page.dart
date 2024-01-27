import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:my_tutorial_app/components/comment.dart';
import 'package:my_tutorial_app/main.dart';
import 'package:my_tutorial_app/components/comment_button.dart';
import 'package:my_tutorial_app/pages/profile_page_edit.dart';
import 'package:my_tutorial_app/pages/profile_page_view.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:open_file_plus/open_file_plus.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:io';

import 'package:http/http.dart' as http;
import 'dart:developer' as developer;

class CommentPage extends StatelessWidget {
  const CommentPage({super.key, required this.id, required this.user});

  final int id;
  final String user;

  @override
  Widget build(BuildContext context) {
    //go back to home page button
    void goBacktoHomePage() {
      Navigator.of(context).pushReplacement(MaterialPageRoute(
        builder: (context) => HomePage(),
      ));
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Comments'),
        leading: GestureDetector(
          onTap: () => goBacktoHomePage(),
          child: const Icon(
            Icons.arrow_back,
          ),
        ),
        backgroundColor: Colors.purple,
      ),
      body: FutureBuilder<List<Comment>>(
        future: fetchComments(id),
        builder: (context, snapshot) {
          if (snapshot.hasError) {
            print('Error: ${snapshot.error}');
            return const Center(
              child: Text('An error has occurred!'),
            );
          } else if (snapshot.hasData) {
            return CommentList(
              comments: snapshot.data!,
              id: id,
              user: user,
            );
          } else {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }
        },
      ),
    );
  }
}

Future<List<Comment>> fetchComments(int id) async {
  final sessionKey = cache.getStringValue("sessionKey");
  final response = await http.get(Uri.parse(
      'https://team-snems.dokku.cse.lehigh.edu/$sessionKey/messages/$id/comments'));

  // Use the compute function to run parsePhotos in a separate isolate.
  // return compute(parseComment, response.body);
  if (response.statusCode == 200) {
    // If the server did return a 200 OK response,
    // then parse the JSON
    return parseComment(response.body);
  } else {
    // If the server did not return a 200 OK response,
    // then throw an exception.
    throw Exception('Failed to load Comments');
  }
}

List<Comment> parseComment(String responseBody) {
  final parsed =
      (jsonDecode(responseBody)['mData'] as List).cast<Map<String, dynamic>>();
  return parsed.map<Comment>((json) => Comment.fromJson(json)).toList();
}

final commentController = TextEditingController();

class CommentList extends StatelessWidget {
  const CommentList(
      {super.key,
      required this.comments,
      required this.id,
      required this.user});

  final int id;
  final String user;
  final List<Comment> comments;

  //show a dialogue box for inputting comment
  void editCommentMenu(BuildContext context, int cId, String oldContent) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: Text("Edit Comment"),
              content: TextField(
                controller: commentController,
                decoration: InputDecoration(hintText: oldContent),
              ),
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
                //save button
                TextButton(
                    onPressed: () {
                      //add comment
                      saveComment(commentController.text, cId, context);
                      //clear controller
                      commentController.clear();
                    },
                    child: Text("Save")),
              ],
            ));
  }

  void saveComment(String content, int cId, BuildContext context) async {
    final sessionKey = cache.getStringValue("sessionKey");
    final response = await http.put(
      Uri.parse(
          'https://team-snems.dokku.cse.lehigh.edu/$sessionKey/comments/$cId'),
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: jsonEncode(<String, String>{
        'mContent': content,
      }),
    );

    if (response.statusCode == 200) {
      Navigator.pop(context);
      Navigator.of(context).pushReplacement(MaterialPageRoute(
        builder: (context) => CommentPage(id: id, user: user),
      ));
    } else {
      throw Exception('Failed to update comment.');
    }
  }

  List<Widget> getChildren(context, comment) {
    List<Widget> children = [
      ViewUserButton(
          onTap: () => seeProfile(comment.userName, context),
          user: comment.userName),
      Padding(
        padding: const EdgeInsets.only(left: 8.0),
        child: Text(comment.commentContent),
      )
    ];
    return getDropdown(children, comment, context);
  }

  List<Widget> getDropdown(children, comment, context) {
    List<PopupMenuEntry> dropdown = [];
    if (comment.userName == user) {
      dropdown.add(const PopupMenuItem<int>(
        value: 0,
        child: Row(children: [
          Icon(
            Icons.edit,
            color: Colors.grey,
          ),
          Text("Edit Comment")
        ]),
      ));
    }
    if (comment.link != null && comment.link != "") {
      dropdown.add(const PopupMenuItem<int>(
        value: 1,
        child: Row(children: [
          Icon(
            Icons.open_in_new,
            color: Colors.grey,
          ),
          Text("Open Link")
        ]),
      ));
    }
    if (comment.b64 != null && comment.b64 != "") {
      dropdown.add(const PopupMenuItem<int>(
        value: 2,
        child: Row(children: [
          Icon(
            Icons.file_open_rounded,
            color: Colors.grey,
          ),
          Text("Open File")
        ]),
      ));
    }

    if (dropdown.isNotEmpty) {
      children.add(PopupMenuButton(
          child: const Icon(Icons.more_vert),
          itemBuilder: (context) {
            return dropdown;
          },
          onSelected: (value) {
            if (value == 0) {
              editCommentMenu(
                  context, comment.commentId, comment.commentContent);
            } else if (value == 1) {
              openLink(comment.link);
            } else if (value == 2) {
              openFile(comment.b64, comment.filename);
            }
          }));
    }

    return children;
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
    return ListView.separated(
      padding: const EdgeInsets.all(8),
      itemCount: comments.length,
      itemBuilder: (context, index) {
        final comment = comments[index];
        List<Widget> children = getChildren(context, comment);
        if (comment.userName == user) {
          return Container(
              height: 50,
              color: Colors.amber[300],
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: children,
              ));
        } else {
          return Container(
              height: 50,
              color: Colors.amber[300],
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: children,
              ));
        }
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }

  void seeProfile(String username, BuildContext context) async {
    String? sessionKey = cache.getStringValue("sessionKey");
    if (username == user) {
      final response = await http.get(Uri.parse(
          'https://team-snems.dokku.cse.lehigh.edu/$sessionKey/users/$username'));
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
      final response = await http.get(Uri.parse(
          'https://team-snems.dokku.cse.lehigh.edu/$sessionKey/users/$username'));
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
}
