import 'comment.dart';

class Message{
  final int messageId;
  final String messageContent;
  final int messageUpvotes;
  final int messaseDownvotes;
  final String messageUsername;
  final List<Comment> messageComment;

  const Message({
    required this.messageId,
    required this.messageContent,
    required this.messageUpvotes,
    required this.messaseDownvotes,
    required this.messageUsername,
    required this.messageComment,
  });

  factory Message.fromJson(Map<String, dynamic> json){
    final messageId = json['mId'];
    if(messageId is! int){
      throw FormatException(
          'Invalid JSON: required "messageId" field of type int in $json');
    }

    final messageContent = json['mContent'];
    if(messageContent is! String){
      throw FormatException(
          'Invalid JSON: required "MessageContent" field of type String in $json');
    }

    final messageUpvotes = json['mUpvotes'];
    if(messageUpvotes is! int){
      throw FormatException(
          'Invalid JSON: required "messageUpvotes" field of type int in $json');
    }

    final messageDownvotes = json['mDownvotes'];
    if(messageDownvotes is! int){
      throw FormatException(
          'Invalid JSON: required "messageUpvotes" field of type int in $json');
    }

    final messageUsername = json['uUsername'];
    if(messageUsername is! String){
      throw FormatException(
          'Invalid JSON: required "messageUsername" field of type String in $json');
    }

    final commentsData = json['mComments'] as List<dynamic>?;
    
    return Message(
      messageId: messageId, 
      messageContent: messageContent, 
      messageUpvotes: messageUpvotes, 
      messaseDownvotes: messageDownvotes, 
      messageUsername: messageUsername, 
      //I don't know if messageComment field is correct
      messageComment: commentsData != null
          ? commentsData
          // map each commentData to a Comment object
          .map((commentData) =>
            Comment.fromJson(commentData as Map<String, dynamic>))
            .toList() // map() returns an Iterable so we convert it to a List
          : <Comment>[], // use an empty list as fallback value
      );
  }
   Map<String, dynamic> toJson() {
    return {
      'mId': messageId,
      'mContent': messageContent,
      'mUpvotes': messageUpvotes,
      'mDownvotes': messaseDownvotes,
      'uUsername': messageUsername,
      'mComments': messageComment.map((Comment) => Comment.toJson()).toList(),
    };
  }

  // @override
  // List<Object?> get props => [messageId, messageContent, messageUpvotes, messaseDownvotes, messageUsername, messageComment];

  // @override
  // bool? get stringify => true;

}