import 'dart:io';
import 'package:http/http.dart' as http;

// Checks for actual internet access
Future<bool> hasRealInternet() async {
  final testUrls = [
    'http://connect.rom.miui.com/generate_204', // MIUI
    'http://captive.apple.com', // Apple
    'http://example.com', // Lightweight
    'http://baidu.com', // China
    'http://clients3.google.com/generate_204', // Android
    'http://www.msftconnecttest.com/connecttest.txt', // Windows
    'http://www.gstatic.com/generate_204', // Google lightweight
    'https://example.com', // HTTPS fallback
  ];

  try {
    final dnsResult = await InternetAddress.lookup('example.com')
        .timeout(const Duration(seconds: 3));
    if (dnsResult.isEmpty || dnsResult[0].rawAddress.isEmpty) return false;

    for (final url in testUrls) {
      try {
        final response =
            await http.get(Uri.parse(url)).timeout(const Duration(seconds: 3));
        if (response.statusCode == 204 ||
            response.statusCode == 200 ||
            response.statusCode == 302) {
          return true;
        }
      } catch (_) {
        // try next
      }
    }
    return false;
  } catch (_) {
    return false;
  }
}
