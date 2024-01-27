import 'dart:developer' as developer;

class Comment {
  final int commentId;
  final String userName;
  final String commentContent;
  final String? link;
  final String? b64;
  final String? filename;

  const Comment({
    required this.commentId,
    required this.userName,
    required this.commentContent,
    required this.link,
    required this.b64,
    required this.filename,
  });

  factory Comment.fromJson(Map<String, dynamic> json) {
    final commentId = json['cId'] as int;
    final userName = json['uUsername'] as String;
    final commentContent = json['cContent'] as String;
    final link = json["cLink"] as String?;
    final filename = json["cFilename"] as String?;
    final b64 = json["cBase64"] as String?;

    return Comment(
        commentId: commentId,
        userName: userName,
        commentContent: commentContent,
        link: link,
        b64: b64,
        filename: filename);
  }

  Map<String, dynamic> toJson() {
    return {
      'cId': commentId,
      'uUsername': userName,
      'cContent': commentContent,
      "cLink": link,
      "cBase64": b64,
      "cFilename": filename,
    };
  }
  //I don't really know what these do
  // @override
  // List<Object?> get props => [commentId, userName, commentContent];

  // @override
  // bool? get stringify => true;
}
