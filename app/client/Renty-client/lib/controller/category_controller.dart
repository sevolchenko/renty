import 'package:client/bloc/categoty/category_bloc.dart';
import 'package:client/bloc/categoty/category_event.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class CategoryController {
  final BuildContext context;
  CategoryController({required this.context});

  void initCategories() async {
    context.read<CategoryBloc>().add(CategoriesLoadEvent());
  }
}
