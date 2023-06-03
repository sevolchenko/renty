abstract class RentEvent {
  const RentEvent();
}

class MyRentsLoadEvent extends RentEvent {}

class UserRentsLoadEvent extends RentEvent {
  final int userId;

  UserRentsLoadEvent(this.userId);
}

class FinishRentsEvent extends RentEvent {
  final int userId;
  final List<int> rentsIds;

  FinishRentsEvent(this.userId, this.rentsIds);
}

class StartRentEvent extends RentEvent {
  final int productId;
  final String sizeName;
  final int count;
  final String startTime;
  final String endTime;

  StartRentEvent(
      {required this.productId,
      required this.sizeName,
      required this.count,
      required this.startTime,
      required this.endTime});
}

class ProlongRentEvent extends RentEvent {
  final int rentId;
  final String newEndTime;

  ProlongRentEvent(this.rentId, this.newEndTime);
}

class SearchUserRentsEvent extends RentEvent {
  // final int userId;
  final String userEmail;
  SearchUserRentsEvent(this.userEmail);
}