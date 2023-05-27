import 'package:client/api/dto/response/product.dart';

abstract class ProductEvent {
  const ProductEvent();
}

class ProductLoadEvent extends ProductEvent {
  final int productId;
  ProductLoadEvent(this.productId);
}

class ProductsLoadEvent extends ProductEvent {}

// class HomeProductItem extends ProductEvent {
//   const HomeProductItem(this.productItem);
//   final ProductResponse? productItem;
// }