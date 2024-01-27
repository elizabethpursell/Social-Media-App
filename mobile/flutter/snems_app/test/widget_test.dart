import 'package:flutter_test/flutter_test.dart';

import 'package:my_tutorial_app/main.dart';
import 'package:my_tutorial_app/pages/picture_page.dart';
import 'package:my_tutorial_app/pages/square_tile.dart';

void main() {
  testWidgets('Login page on app initialization', (tester) async {
    await tester.pumpWidget(const MyApp());

    final findTitle = find.text('Welcome back you\'ve been missed!');

    expect(findTitle, findsOneWidget);
  });

  testWidgets('Home page hidden on app init', (tester) async {
    await tester.pumpWidget(const MyApp());

    final findTitle = find.text('SNEMS');

    expect(findTitle, findsNothing);
  });

  testWidgets('Home page hidden on app init', (tester) async {
    await tester.pumpWidget(const MyApp());

    final findTitle = find.text('SNEMS');

    expect(findTitle, findsNothing);
  });

  testWidgets('Profile page hidden on app init', (tester) async {
    await tester.pumpWidget(const MyApp());

    final findTitle = find.text('Profile');

    expect(findTitle, findsNothing);
  });

  testWidgets('Picture page hidden on app init', (tester) async {
    await tester.pumpWidget(const MyApp());

    final findTitle = find.text('Gallery and Camera Access');

    expect(findTitle, findsNothing);
  });
}
