import 'package:client/api/apis/product_api.dart';
import 'package:client/api/dto/response/product.dart';
import 'package:client/api/dto/response/product_preview.dart';
import 'package:client/api/dto/response/size.dart';
import 'package:client/common/widgets/auxiliary_wigets.dart';

class ProductRepository {
  final ProductApi _productApi = ProductApi();

  Future<ProductResponse> getProductById(int id) {
    return _productApi.productById(id);
  }

  Future<List<ProductResponse>> getProducts() {
    return _productApi.products();
  }

  Future<List<ProductPreviewResponse>> getProductsPreviews() {
    return _productApi.productsPreviews();
  }

  Future<List<SizeResponse>> getProductSizes(int productId) {
    return _productApi.productSizes(productId);
  }

  Future<void> sizeCountUpdate(int id, Map<String, dynamic> size) async {
    await _productApi.sizeCountUpdate(id, size);
  }

  Future<void> sizeDelete(int id, Map<String, dynamic> size) async {
    await _productApi.sizeDelete(id, size);
  }

  Future<void> sizeCreate(int id, Map<String, dynamic> size) async {
    await _productApi.sizeCreate(id, size);
  }

  Future<void> productCreate(Map<String, dynamic> body) async {
    await _productApi.productCreate(body);
  }

  Future<void> productDelete(int id) async {
    await _productApi.productDelete(id);
  }

}
