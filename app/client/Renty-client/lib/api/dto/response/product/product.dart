import 'package:client/api/dto/response/product/category.dart';
import 'package:client/api/dto/response/product/image.dart';
import 'package:client/api/dto/response/product/size.dart';

class ProductResponse {
  final int id;
  final String name;
  final String description;
  final int price;
  final List<SizeResponse> sizes;
  final CategoryPreviewResponse category;
  final List<ImageResponse> images;

  const ProductResponse(
      {required this.id,
      required this.name,
      required this.description,
      required this.price,
      required this.sizes,
      required this.category,
      required this.images});

  factory ProductResponse.fromJson(Map<String, dynamic> json) {
    return ProductResponse(
        id: json['product_id'],
        name: json['name'],
        description: json['description'],
        price: json['price'],
        sizes: json['sizes'] = List<SizeResponse>.from(
            json['sizes'].map((x) => SizeResponse.fromJson(x))),
        category: json['category'] =
            CategoryPreviewResponse.fromJson(json['category']),
        images: json['images'] = List<ImageResponse>.from(
            json['images'].map((x) => ImageResponse.fromJson(x))));
  }
}
