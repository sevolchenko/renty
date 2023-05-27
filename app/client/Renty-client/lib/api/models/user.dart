class UserModel {
  final int id;
  final String name;
  final String email;
  final String? password;

  UserModel({required this.id, required this.name, required this.email, required this.password});

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
        id: json['user_id'], name: json['name'], email: json['email'],
        password: null);
  }
}