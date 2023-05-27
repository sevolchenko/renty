import 'package:client/common/routes/routes.dart';
import 'package:client/global.dart';
import 'package:client/screens/application/application.dart';
import 'package:client/screens/home/home_screen.dart';
import 'package:client/screens/profile/sign_in/sign_in_screen.dart';
import 'package:flutter/material.dart';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

void main() async {
  await Global.init();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});


  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [...AppScreens.allProviders(context)],
      child: ScreenUtilInit(
        builder: (context, child) => MaterialApp(
          // debugShowCheckedModeBanner: false,

          builder: EasyLoading.init(),
          theme: ThemeData(
              appBarTheme: const AppBarTheme(
                  iconTheme: IconThemeData(
                    color: Colors.black,
                  ),
                  elevation: 0,
                  backgroundColor: Colors.white)),
          title: 'My App',
          // home: const HomeScreen(),
          onGenerateRoute: AppScreens.GenerateRouteSettings,
        ),
      ),
    );
  }
}