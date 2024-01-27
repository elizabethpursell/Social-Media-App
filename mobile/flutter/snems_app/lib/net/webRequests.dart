import 'package:http/http.dart' as http;
import 'dart:convert';

Future<String> fetchData() async {
  final response = await http.get(Uri.parse('https://team-snems.dokku.cse.lehigh.edu/messages'));

  if (response.statusCode == 200) {
    return response.body;
  } else {
    throw Exception('Failed to load data from server');
  }
}

Future<void> postData(Map<String, dynamic> data) async {
  final response = await http.post(
    Uri.parse('https://team-snems.dokku.cse.lehigh.edu/messages'),
    body: jsonEncode(data),
    headers: {'Content-Type': 'application/json'},
  );

  if (response.statusCode != 200) {
    throw Exception('Failed to post data: ${response.statusCode}');
  }
}

Future<void> putData(int messageId, Map<String, dynamic> data) async {
  final response = await http.put(
    Uri.parse('https://team-snems.dokku.cse.lehigh.edu/messages/0'),
    body: jsonEncode(data),
    headers: {'Content-Type': 'application/json'},
  );

  if (response.statusCode != 200) {
    throw Exception('Failed to update data: ${response.statusCode}');
  }
}

Future<void> deleteData(int messageId) async {
  final response = await http.delete(
    Uri.parse('https://team-snems.dokku.cse.lehigh.edu/messages/0'),
  );

  if (response.statusCode != 200) {
    throw Exception('Failed to delete data: ${response.statusCode}');
  }
}