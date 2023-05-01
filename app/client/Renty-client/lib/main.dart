import 'package:flutter/material.dart';

import 'package:client/screens/registration/registration_screen.dart';
import 'package:client/screens/authorizathion/authorization_screen.dart';
void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: LoginScreen(),
    );
  }
}