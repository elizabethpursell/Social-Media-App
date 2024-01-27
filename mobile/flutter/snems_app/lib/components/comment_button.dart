import 'package:flutter/material.dart';

class CommentButton extends StatelessWidget{
  final void Function()? onTap;
  const CommentButton({super.key, required this.onTap});

  @override
  Widget build(BuildContext context){
    return GestureDetector(
      onTap: onTap,
      child: const Icon(
        Icons.add_comment,
        color: Colors.grey,
      )
    );
  }
}

class AllCommentButton extends StatelessWidget{
  final void Function()? onTap;
  const AllCommentButton ({super.key, required this.onTap});

  @override
  Widget build(BuildContext context){
    return GestureDetector(
      onTap: onTap,
      child: const Icon(
        Icons.comment,
        color:Colors.grey,
      )
    );
  }
}

class EditCommentButton extends StatelessWidget{
  final void Function()? onTap;
  const EditCommentButton({super.key, required this.onTap});

  @override
  Widget build(BuildContext context){
    return GestureDetector(
      onTap: onTap,
      child: const Icon(
        Icons.edit,
        color: Colors.grey,
      )
    );
  }
}
