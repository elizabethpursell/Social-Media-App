import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:my_tutorial_app/main.dart';

// reference: https://www.geeksforgeeks.org/gallery-access-in-flutter/

class GalleryAccess extends StatefulWidget {
  const GalleryAccess({super.key});

  @override
  State<GalleryAccess> createState() => _GalleryAccessState();
}

class _GalleryAccessState extends State<GalleryAccess> {
  File? galleryFile;
  final picker = ImagePicker();
  
  @override
  Widget build(BuildContext context) {
    //display image selected from gallery
    var buttons = const Row(mainAxisAlignment: MainAxisAlignment.spaceAround);
    if (galleryFile != null) {
      buttons =
          Row(mainAxisAlignment: MainAxisAlignment.spaceAround, children: [
        TextButton(
          style: TextButton.styleFrom(
            foregroundColor: Colors.purple, // Text Color
          ),
          onPressed: () {
            tempFile = galleryFile;
            Navigator.pop(context);
          },
          child: const Text("Attach"),
        ),
        TextButton(
            style: TextButton.styleFrom(
              foregroundColor: Colors.purple, // Text Color
            ),
            onPressed: () {
              tempFile = null;
              Navigator.pop(context);
            },
            child: const Text("Cancel")),
      ]);
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Gallery and Camera Access'),
        backgroundColor: Colors.purple,
        actions: const [],
      ),
      body: Builder(
        builder: (BuildContext context) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  style: ButtonStyle(
                      backgroundColor:
                          MaterialStateProperty.all(Colors.purple)),
                  child: const Text('Select Image from Gallery and Camera'),
                  onPressed: () {
                    _showPicker(context: context);
                  },
                ),
                const SizedBox(
                  height: 20,
                ),
                SizedBox(
                    height: 200.0,
                    width: 300.0,
                    child: galleryFile == null
                        ? const Center(child: Text('Sorry nothing selected!!'))
                        : Center(child: Image.file(galleryFile!))),
                buttons
              ],
            ),
          );
        },
      ),
    );
  }

  void _showPicker({
    required BuildContext context,
  }) {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return SafeArea(
          child: Wrap(
            children: <Widget>[
              ListTile(
                leading: const Icon(Icons.photo_library),
                title: const Text('Photo Library'),
                onTap: () {
                  getImage(ImageSource.gallery);
                  Navigator.of(context).pop();
                },
              ),
              ListTile(
                leading: const Icon(Icons.photo_camera),
                title: const Text('Camera'),
                onTap: () {
                  getImage(ImageSource.camera);
                  Navigator.of(context).pop();
                },
              ),
            ],
          ),
        );
      },
    );
  }

  Future getImage(
    ImageSource img,
  ) async {
    final pickedFile = await picker.pickImage(source: img);
    XFile? xfilePick = pickedFile;
    setState(
      () {
        if (xfilePick != null) {
          galleryFile = File(pickedFile!.path);
        } else {
          ScaffoldMessenger.of(context).showSnackBar(// is this context <<<
              const SnackBar(content: Text('Nothing is selected')));
        }
      },
    );
  }
}