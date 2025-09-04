import 'package:connectivity_plus/connectivity_plus.dart';

Future<bool> hasNetworkConnection() async {
  final result = await Connectivity().checkConnectivity();

  if (result is List) {
    final first = result.isNotEmpty ? result.first : ConnectivityResult.none;
    return first == ConnectivityResult.mobile ||
        first == ConnectivityResult.wifi;
  } else {
    return result == ConnectivityResult.mobile ||
        result == ConnectivityResult.wifi;
  }
}
