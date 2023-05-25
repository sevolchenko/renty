import 'dart:convert';

import 'package:client/api/dto/response/product.dart';
import 'package:client/common/values/colors.dart';
import 'package:client/common/widgets/button_widget.dart';
import 'package:client/common/widgets/text/text_widgets.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';


Widget buildProduct(ProductResponse product) {
  var image = product.images![0].image;
  List<String> size_names = [];
  product.sizes!.forEach((size) {
    size_names.add(size.sizeName);
  });
  return Column(
    children: [
      Container(
        width: 200.w,
        height: 200.h,
        decoration: BoxDecoration(
          border: Border.all(color: kPrimaryColor, width: 4),
        ),
        child: Image.memory(
          base64Decode(product.images![0].image),
          width: 130.h,
          height: 130.h,
        ),
      ),
      buildTextInfo("Название", product.name!),
      Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.all(Radius.circular(10.w)),
          border: Border.all(color: kPrimaryColor, width: 2),
        ),
        margin: EdgeInsets.symmetric(horizontal: 10.w, vertical: 20.h),
        padding: EdgeInsets.all(10.w),
        child:
            reusableText(product.description ?? "", Colors.black, textSize: 16),
      ),
      buildTextInfo("Цена", "${product.price} руб/час"),
      SizedBox(height: 20.h),
      DropdownButton(
        items: size_names.map<DropdownMenuItem<String>>((String value) {
          return DropdownMenuItem<String>(
            value: value,
            child: Text(value),
          );
        }).toList(),
        onChanged: (String? value) {
        },
      ),
      reusableText('Время начала аренды', Colors.black, textSize: 20),
      buildTextField('24.04.2023, 16:00', 'time', bottomMargin: 0, (value) {}),
      SizedBox(height: 20.h),
      reusableText('Время окончания аренды', Colors.black, textSize: 20),
      buildTextField('24.04.2023, 17:00', 'time', bottomMargin: 0, (value) {}),
      buildTextInfo("Длительность аренды", "01:00:00"),
      SizedBox(
        height: 10.h,
      ),
      buildButton("Уведомить по освобождении", "secondary", () {}),
      SizedBox(
        height: 20.h,
      ),
      buildButton("Добавить в корзину", "primary", () {}),
      SizedBox(
        height: 20.h,
      ),
    ],
  );
}
