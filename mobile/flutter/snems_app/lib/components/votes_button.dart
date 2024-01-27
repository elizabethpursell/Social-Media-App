import 'package:flutter/material.dart';
import 'dart:developer' as developer;

class VoteButton extends StatefulWidget {
  final void Function()? onTap;
  final int votes;
  final String voteStatus;
  final String voteType;

  const VoteButton(
      {Key? key,
      required this.onTap,
      required this.votes,
      required this.voteStatus,
      required this.voteType})
      : super(key: key);

  @override
  _VoteButtonState createState() => _VoteButtonState();
}

class _VoteButtonState extends State<VoteButton> {
  String isVoted = "none";

  @override
  Widget build(BuildContext context) {
    isVoted = widget.voteStatus;
    return GestureDetector(
      onTap: () {
        setState(() {
          isVoted = widget.voteStatus;
        });
        if (widget.onTap != null) {
          widget.onTap!();
        }
      },
      child: Column(
        children: [
          getIcon(),
          Text('${widget.votes}'),
        ],
      ),
    );
  }

  Icon getIcon() {
    var icon = Icon(
      Icons.thumb_up,
      color: (isVoted == "upvoted") ? Colors.red : Colors.grey,
    );
    if (widget.voteType == "downvote") {
      icon = Icon(
        Icons.thumb_down,
        color: (isVoted == "downvoted") ? Colors.black : Colors.grey,
      );
    }
    return icon;
  }
}
