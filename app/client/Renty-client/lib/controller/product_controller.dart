import 'package:client/api/apis/product_api.dart';
import 'package:client/api/dto/response/product.dart';
import 'package:client/bloc/product/product_bloc.dart';
import 'package:client/bloc/product/product_event.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class ProductController {
  final BuildContext context;
  ProductController({required this.context});

  void init(int productId) async {
    EasyLoading.show(
      indicator: CircularProgressIndicator(),
      maskType: EasyLoadingMaskType.clear,
      dismissOnTap: true,
    );
    var result = await ProductApi.productById(productId);
    context.read<ProductBloc>().add(HomeProductItem(result));
    EasyLoading.dismiss();
  }

  Future<ProductResponse> getProduct(int productId) async {
    final result = await ProductApi.productById(productId);
    return result;
  }
}
