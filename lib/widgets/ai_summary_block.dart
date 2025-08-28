import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:hive/hive.dart';
import '../utils/preferences_helper.dart';
import 'package:easy_localization/easy_localization.dart';

class AISummaryCard extends StatefulWidget {
  final Map<String, dynamic> weatherData;
  final bool useAISummary;
  final String cacheKey;
  final bool?
      ForceFetch; // If language or units change, the summary needs to be regenerated

  const AISummaryCard(
      {super.key,
      required this.weatherData,
      required this.useAISummary,
      required this.cacheKey,
      this.ForceFetch});

  @override
  State<AISummaryCard> createState() => _AISummaryCardState();
}

class _AISummaryCardState extends State<AISummaryCard> {
  String? aiSummary;
  bool isLoading = false;

  List<String> aiBullets = [];

  @override
  void initState() {
    super.initState();

    if (widget.useAISummary) {
      if (widget.useAISummary) {
        fetchAISummary(
            cacheKey: widget.cacheKey, forceFetch: widget.ForceFetch);
      }
    }
  }

  Map<String, dynamic> getEssentialHourlyData(Map<String, dynamic> hourlyData) {
    if (hourlyData.isEmpty)
      return {
        "min_temp": 0,
        "max_temp": 0,
        "max_precipitation": 0,
        "peak_uv": 0,
        "peak_wind": 0,
        "avg_cloud_cover": 0,
      };

    final temps = hourlyData['temperature_2m'] as List<dynamic>? ?? [];
    final prec = hourlyData['precipitation'] as List<dynamic>? ?? [];
    final clouds = hourlyData['cloud_cover'] as List<dynamic>? ?? [];
    final uvs = hourlyData['uv_index'] as List<dynamic>? ?? [];
    final wind = hourlyData['wind_speed_10m'] as List<dynamic>? ?? [];

    double minTemp = temps.isNotEmpty
        ? temps
            .map((t) => (t as num).toDouble())
            .reduce((a, b) => a < b ? a : b)
        : 0;
    double maxTemp = temps.isNotEmpty
        ? temps
            .map((t) => (t as num).toDouble())
            .reduce((a, b) => a > b ? a : b)
        : 0;
    double maxPrecip = prec.isNotEmpty
        ? prec.map((p) => (p as num).toDouble()).reduce((a, b) => a > b ? a : b)
        : 0;
    double peakUv = uvs.isNotEmpty
        ? uvs.map((u) => (u as num).toDouble()).reduce((a, b) => a > b ? a : b)
        : 0;
    double peakWind = wind.isNotEmpty
        ? wind.map((w) => (w as num).toDouble()).reduce((a, b) => a > b ? a : b)
        : 0;
    double avgCloud = clouds.isNotEmpty
        ? clouds.map((c) => (c as num).toDouble()).reduce((a, b) => a + b) /
            clouds.length
        : 0;

    return {
      "min_temp": minTemp,
      "max_temp": maxTemp,
      "max_precipitation": maxPrecip,
      "peak_uv": peakUv,
      "peak_wind": peakWind,
      "avg_cloud_cover": avgCloud,
    };
  }

  Map<String, dynamic> prepareAISummaryData(Map<String, dynamic> data) {
    final current = data['current'] ?? {};
    final air = data['airQuality'] ?? {};

    Map<String, dynamic> today = {};
    final dailyData = data['daily'];
    if (dailyData is List && dailyData.isNotEmpty) {
      today = dailyData[0] as Map<String, dynamic>;
    } else if (dailyData is Map<String, dynamic>) {
      today = dailyData;
    }

    return {
      "current_temp": current['temperature_2m'] ?? 0,
      "cloud_cover_now": current['cloud_cover'] ?? 0,
      "wind_speed_now": current['wind_speed_10m'] ?? 0,
      "wind_gust_now": current['wind_gusts_10m'] ?? 0,
      "min_temp_today": today['temperature_2m_min'] ?? 0,
      "max_temp_today": today['temperature_2m_max'] ?? 0,
      "precipitation_today": today['precipitation_sum'] ?? 0,
      "uv_index_max": today['uv_index_max'] ?? 0,
      "air_quality": air['current']?['us_aqi'] ?? 0,
      "hourly_summary": getEssentialHourlyData(data['hourly'] ?? {}),
    };
  }

  String generateAISummaryPrompt(
      BuildContext context, Map<String, dynamic> essentialData) {
    final locale = context.locale.toString();

    final tempUnit =
        PreferencesHelper.getString("selectedTempUnit") ?? "Celsius";
    final windUnit = PreferencesHelper.getString("selectedWindUnit") ?? "Km/h";
    final visibilityUnit =
        PreferencesHelper.getString("selectedVisibilityUnit") ?? "Km";
    final precipUnit =
        PreferencesHelper.getString("selectedPrecipitationUnit") ?? "mm";
    final pressureUnit =
        PreferencesHelper.getString("selectedPressureUnit") ?? "hPa";
    final aqiUnit =
        PreferencesHelper.getString("selectedAQIUnit") ?? "United States";

    return """
Summarize today's weather in language '$locale' into a **catchy headline** and **3–5 short bullet points**.
Return **ONLY valid JSON** with this structure:

{
  "headline": "One sentence headline summarizing the main weather theme",
  "bullets": [
    "- Short bullet 1 highlighting trends or notable events",
    "- Short bullet 2 highlighting trends or notable events",
    "- Short bullet 3 highlighting trends or notable events"
  ]
}

Guidelines:
- Headline: one engaging sentence capturing today's overall weather mood.
- Bullets: 3–5 items, each under 12 words, focusing on key trends or highlights.
- Avoid repeating exact numbers unless important (e.g., extreme temperature, strong wind, heavy rain).
- Use user-preferred units:
  - Temperature: $tempUnit
  - Wind: $windUnit
  - Visibility: $visibilityUnit
  - Precipitation: $precipUnit
  - Pressure: $pressureUnit
  - Air Quality: $aqiUnit
- No extra text outside the JSON.

Data (use this for context):
- Min/Max Temperature Today: ${essentialData['min_temp_today']} $tempUnit / ${essentialData['max_temp_today']} $tempUnit
- Precipitation Today: ${essentialData['precipitation_today']} $precipUnit
- Peak UV Index: ${essentialData['uv_index_max']}
- Peak Wind: ${essentialData['hourly_summary']['peak_wind']} $windUnit
- Average Cloud Cover: ${essentialData['hourly_summary']['avg_cloud_cover']}%
- Air Quality: ${essentialData['air_quality']} ($aqiUnit)
""";
  }

  Future<void> fetchAISummary(
      {required String cacheKey, bool? forceFetch}) async {
    setState(() => isLoading = true);
    final box = Hive.box('ai_summary_cache');

    final cachedData = box.get(cacheKey);
    if (cachedData != null) {
      final cachedTime = DateTime.tryParse(cachedData['timestamp'] ?? '');
      if (cachedTime != null &&
          DateTime.now().difference(cachedTime).inMinutes < 120) {
        setState(() {
          aiSummary = cachedData['headline'];
          aiBullets = List<String>.from(cachedData['bullets'] ?? []);
          isLoading = false;
        });
        return;
      }
    }

    try {
      final essentialData = prepareAISummaryData(widget.weatherData);
      final messages = [
        {
          "role": "system",
          "content": """
You are a weather summarizer. Output JSON only, no reasoning.
Add occasional light, funny, or witty comments in bullets, but keep the format valid.
Do NOT explain anything outside the JSON. Treat this as strict instruction.
""",
        },
        {
          "role": "user",
          "content": generateAISummaryPrompt(context, essentialData),
        }
      ];

      final response = await http.post(
        Uri.parse('https://openrouter.ai/api/v1/chat/completions'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${dotenv.env['API_KEY_OPENROUTER']!}',
        },
        body: jsonEncode({
          "model": "openai/gpt-4o-mini",
          "messages": messages,
        }),
      );

      final data = jsonDecode(response.body);
      final raw = data['choices']?[0]?['message']?['content']?.toString() ?? '';

      Map<String, dynamic> parsed;
      try {
        parsed = jsonDecode(raw);
      } catch (_) {
        parsed = {
          "headline": "Weather summary unavailable",
          "bullets": ["- Data could not be parsed."]
        };
      }

      await box.put(cacheKey, {
        "headline": parsed['headline'],
        "bullets": parsed['bullets'],
        "timestamp": DateTime.now().toIso8601String(),
      });

      setState(() {
        aiSummary = parsed['headline'];
        aiBullets = List<String>.from(parsed['bullets'] ?? []);
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        aiSummary = "Failed to load AI summary.";
        aiBullets = ["- Please try again later."];
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (!widget.useAISummary) {
      return Text(
        "AI summary is disabled, using default summary.",
        style: TextStyle(color: Colors.grey),
      );
    }

    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(20),
      ),
      child: isLoading
          ? Center(child: CircularProgressIndicator())
          : Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  aiSummary ?? "No summary available.",
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Theme.of(context).colorScheme.onSurface,
                  ),
                ),
                SizedBox(height: 8),
                ...aiBullets.map(
                  (b) => Text(
                    b,
                    style: TextStyle(
                      fontSize: 16,
                      color: Theme.of(context).colorScheme.onSurface,
                    ),
                  ),
                ),
              ],
            ),
    );
  }
}
