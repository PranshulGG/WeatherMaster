import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../utils/open_links.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:hive/hive.dart';
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class AboutPage extends StatefulWidget {
  const AboutPage({super.key});

  @override
  State<AboutPage> createState() => _AboutPageState();
}

class _AboutPageState extends State<AboutPage> {
  final ScrollController _scrollController = ScrollController();

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final colorTheme = Theme.of(context).colorScheme;
    return Scaffold(
      backgroundColor: colorTheme.surfaceContainer,
      appBar: AppBar(
        automaticallyImplyLeading: false,
        backgroundColor: Theme.of(context).colorScheme.surface,
        elevation: 0,
        toolbarHeight: 120,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(28),
                bottomRight: Radius.circular(28))),
        title: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
                spacing: 10,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "WeatherMaster",
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.onSurface,
                        fontSize: 24),
                  ),
                  Row(
                    spacing: 2,
                    children: [
                      CheckUpdateButton(),
                      FilledButton.tonal(
                        onPressed: () {
                          showModalBottomSheet(
                            context: context,
                            isScrollControlled: true,
                            backgroundColor: Colors.transparent,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.vertical(
                                  top: Radius.circular(28)),
                            ),
                            builder: (context) {
                              return DraggableScrollableSheet(
                                initialChildSize: 0.4,
                                minChildSize: 0.4,
                                maxChildSize: 0.9,
                                snap: true,
                                builder: (context, scrollController) {
                                  return Scaffold(
                                    backgroundColor: Colors.transparent,
                                    appBar: AppBar(
                                      toolbarHeight: 65,
                                      elevation: 0,
                                      automaticallyImplyLeading: false,
                                      shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.only(
                                              topLeft: Radius.circular(28),
                                              topRight: Radius.circular(28))),
                                      title: Text("changelog".tr()),
                                      backgroundColor:
                                          colorTheme.surfaceContainerLow,
                                      scrolledUnderElevation: 1,
                                      actions: [
                                        IconButton.filledTonal(
                                            onPressed: () {
                                              Navigator.pop(context);
                                            },
                                            icon: Icon(Icons.close)),
                                        SizedBox(
                                          width: 10,
                                        )
                                      ],
                                    ),
                                    body: Container(
                                      decoration: BoxDecoration(
                                        color: colorTheme.surfaceContainerLow,
                                        boxShadow: [
                                          BoxShadow(
                                            blurRadius: 10,
                                            color: Colors.black26,
                                            offset: Offset(0, -2),
                                          ),
                                        ],
                                      ),
                                      child: ChangelogSheet(
                                          scrollController: scrollController),
                                    ),
                                  );
                                },
                              );
                            },
                          );
                        },
                        style: ButtonStyle(
                          padding: WidgetStateProperty.all(
                            EdgeInsets.symmetric(horizontal: 12, vertical: 5),
                          ),
                          shape: WidgetStateProperty.all(
                            RoundedRectangleBorder(
                              borderRadius: BorderRadius.only(
                                  topLeft: Radius.circular(3),
                                  topRight: Radius.circular(50),
                                  bottomLeft: Radius.circular(3),
                                  bottomRight: Radius.circular(50)),
                            ),
                          ),
                          minimumSize: WidgetStateProperty.all(Size(0, 30)),
                          iconAlignment: IconAlignment.end,
                          tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                        ),
                        child: Text("whats_new".tr(),
                            style: TextStyle(fontWeight: FontWeight.w700)),
                      ),
                    ],
                  )
                ]),
            Container(
                clipBehavior: Clip.hardEdge,
                decoration:
                    BoxDecoration(borderRadius: BorderRadius.circular(50)),
                child: Image.asset(
                  "assets/weather-icons/new_icon.png",
                  width: 76,
                  height: 76,
                ))
          ],
        ),
      ),
      body: RawScrollbar(
        thumbVisibility: true,
        controller: _scrollController,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(50)),
        thumbColor: colorTheme.outlineVariant,
        thickness: 3,
        child: ListView(
          controller: _scrollController,
          primary: false,
          children: [
            SizedBox(
              height: 10,
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(radius: 23, child: Icon(Symbols.license)),
              title: Text("licenses".tr()),
              subtitle: Text("GNU GPL-3.0"),
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.mail),
              ),
              title: Text("email_text".tr()),
              subtitle: Text("pranshul.devmain@gmail.com"),
              onTap: () {
                openLink("mailto:pranshul.devmain@gmail.com");
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.code),
              ),
              title: Text("source_code".tr()),
              subtitle: Text("on_github".tr()),
              onTap: () {
                openLink("https://github.com/PranshulGG/WeatherMaster");
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.bug_report),
              ),
              title: Text("create_an_issue".tr()),
              subtitle: Text("on_github".tr()),
              onTap: () {
                openLink("https://github.com/PranshulGG/WeatherMaster/issues/");
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.apps),
              ),
              title: Text("more_apps".tr()),
              subtitle: Text("view".tr()),
              onTap: () {
                showModalBottomSheet(
                  context: context,
                  showDragHandle: true,
                  shape: RoundedRectangleBorder(
                    borderRadius:
                        BorderRadius.vertical(top: Radius.circular(28)),
                  ),
                  builder: (ctx) {
                    return StatefulBuilder(builder: (ctx2, setSt) {
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          SettingSection(styleTile: true, tiles: [
                            SettingActionTile(
                                icon: CircleAvatar(
                                  child: SvgPicture.string(
                                      ''' <svg width="221" height="222" viewBox="0 0 221 222" fill="none" xmlns="http://www.w3.org/2000/svg">
<circle cx="110.5" cy="110.528" r="110.5" fill="#F3F1E8"/>
<path d="M111 111.028L55 188.028" stroke="#2B1F0D" stroke-width="2" stroke-linecap="round"/>
<path d="M188 55.0278L112 110.028" stroke="#32351E" stroke-width="5" stroke-linecap="round"/>
<path d="M63 78.0278L112 114.028" stroke="#4C4400" stroke-width="16" stroke-linecap="round"/>
</svg>
 '''),
                                ),
                                title: Text("ClockMaster"),
                                description: Text("view_on_github".tr()),
                                trailing: Icon(Symbols.open_in_new),
                                onTap: () {
                                  openLink(
                                      "https://github.com/PranshulGG/ClockMaster");
                                }),
                          ]),
                          Padding(
                            padding: EdgeInsets.only(
                              top: 16,
                              bottom:
                                  MediaQuery.of(context).padding.bottom + 10,
                              left: 16,
                              right: 16,
                            ),
                            child: SizedBox(
                              width: double.infinity,
                              child: FilledButton(
                                onPressed: () {
                                  Navigator.of(context).pop();
                                },
                                child: Text("close".tr()),
                              ),
                            ),
                          ),
                        ],
                      );
                    });
                  },
                );
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.favorite),
              ),
              title: Text("contributors".tr()),
              subtitle: Text("translators".tr()),
              onTap: () {
                TranslatorsDialog.show(
                  context,
                  projectId: '741419',
                  apiToken: dotenv.env['API_TOKEN']!.toString(),
                );
              },
            ),
          ],
        ),
      ),
      bottomNavigationBar: ClipRRect(
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(28),
          topRight: Radius.circular(28),
        ),
        child: BottomAppBar(
            elevation: 0,
            height: 180,
            padding: EdgeInsets.only(top: 10),
            color: Theme.of(context).colorScheme.surface,
            child: ListView(physics: NeverScrollableScrollPhysics(), children: [
              ListTile(
                leading: CircleAvatar(radius: 23, child: Icon(Symbols.license)),
                title: Text("third_party_licenses".tr()),
                onTap: () {
                  Navigator.of(context).push(
                    PageRouteBuilder(
                      reverseTransitionDuration: Duration(milliseconds: 200),
                      fullscreenDialog: true,
                      pageBuilder: (context, animation, secondaryAnimation) {
                        return LicensePage(
                          applicationName: 'WeatherMaster',
                          applicationVersion: 'v2.6.6 (F)',
                          applicationIcon: Container(
                            clipBehavior: Clip.hardEdge,
                            margin: const EdgeInsets.only(bottom: 16, top: 16),
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(50),
                            ),
                            child: Image.asset(
                              'assets/weather-icons/new_icon.png',
                              width: 60,
                              height: 60,
                            ),
                          ),
                        );
                      },
                      transitionsBuilder:
                          (context, animation, secondaryAnimation, child) {
                        return FadeTransition(
                          opacity: animation,
                          child: child,
                        );
                      },
                    ),
                  );
                },
              ),
              ListTile(
                leading: CircleAvatar(
                  radius: 23,
                  child: Icon(Symbols.mail),
                ),
                title: Text("terms_&_conditions".tr()),
                onTap: () {
                  Navigator.of(context).push(
                    PageRouteBuilder(
                      opaque: true,
                      fullscreenDialog: true,
                      reverseTransitionDuration: Duration(milliseconds: 200),
                      pageBuilder: (context, animation, secondaryAnimation) {
                        return TermsPage();
                      },
                      transitionsBuilder:
                          (context, animation, secondaryAnimation, child) {
                        return FadeTransition(
                          opacity: animation,
                          child: child,
                        );
                      },
                    ),
                  );
                },
              ),
              ListTile(
                leading: CircleAvatar(
                  radius: 23,
                  child: Icon(Symbols.code),
                ),
                title: Text("privacy_policy".tr()),
                onTap: () {
                  Navigator.of(context).push(
                    PageRouteBuilder(
                      opaque: true,
                      fullscreenDialog: true,
                      reverseTransitionDuration: Duration(milliseconds: 200),
                      pageBuilder: (context, animation, secondaryAnimation) {
                        return PolicyPage();
                      },
                      transitionsBuilder:
                          (context, animation, secondaryAnimation, child) {
                        return FadeTransition(
                          opacity: animation,
                          child: child,
                        );
                      },
                    ),
                  );
                },
              ),
            ])),
      ),
    );
  }
}

class TermsPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final markdownData = '''

These Terms & Conditions apply to the **WeatherMaster** app (the "Application") for mobile devices.  
This app was created by **Pranshul** as an open-source, hobby project. By using the Application, you agree to the following:

## Use of the Application
- The Application is provided **as-is**, free of charge, and without any guarantees of reliability, availability, or accuracy.  
- You may use, modify, and distribute the Application in accordance with its open-source license.  
- You may **not** misrepresent the origin of the Application or use its name/trademarks without permission.

## Data & Privacy
- The Application does **not collect, store, or share** any personal information.  
- The only permission requested is **location access**, which is optional and used solely within the Application to provide weather information. This data never leaves your device.  
- For more details, please see the Privacy Policy.

## Liability
- The Service Provider (Pranshul) is **not liable** for any direct or indirect damages, losses, or issues that may arise from using the Application.  
- This includes (but is not limited to) inaccurate weather data, device issues, or mobile data charges.  
- You are responsible for ensuring your device is compatible and has sufficient internet and battery to use the Application.

## Updates & Availability
- The Application may be updated from time to time.  
- There is no guarantee that the Application will always remain available, functional, or supported on all operating system versions.  
- The Service Provider may discontinue the Application at any time without prior notice.

## Changes to These Terms
These Terms & Conditions may be updated in the future. Updates will be posted in the project repository or within the Application. Continued use of the Application means you accept any revised terms.

## Contact
If you have any questions about these Terms & Conditions, please contact:  
📧 **pranshul.devmain@gmail.com**



''';

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surfaceContainer,
      appBar: AppBar(
        title: Text('terms_&_conditions'.tr()),
        titleSpacing: 0,
        backgroundColor: Theme.of(context).colorScheme.surfaceContainer,
        scrolledUnderElevation: 0,
      ),
      body: Markdown(
        data: markdownData,
        padding: EdgeInsets.only(
            top: 16,
            bottom: MediaQuery.of(context).padding.bottom + 10,
            left: 16,
            right: 16),
        onTapLink: (text, href, title) async {
          openLink(href.toString());
        },
      ),
    );
  }
}

class PolicyPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final markdownData = '''

WeatherMaster is an open-source application.  

We respect your privacy. This application:  

- Does **not collect, store, or share** any personal information.  
- Does **not track** your IP address, usage data, or any identifiers.  
- Does **not send data** to us or to third parties.  

## Location Permission  
- The app may request access to your device’s **location** in order to show you local weather information.  
- Granting location permission is **optional**.  
- Your location data is used **only within the app** to provide weather information and is **never collected, stored, or shared**.  

## Children  
This application does not knowingly collect any personal information from anyone, including children under 13.  

## Changes  
If this Privacy Policy changes, we will update it here. Continued use of the app after changes means you accept the revised policy.  

## Contact   
If you have any questions about privacy while using WeatherMaster, please contact us at:  
📧 **pranshul.devmain@gmail.com**  
''';

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surfaceContainer,
      appBar: AppBar(
        title: Text('privacy_policy'.tr()),
        titleSpacing: 0,
        backgroundColor: Theme.of(context).colorScheme.surfaceContainer,
        scrolledUnderElevation: 0,
      ),
      body: Markdown(
        data: markdownData,
        padding: EdgeInsets.only(
            top: 16,
            bottom: MediaQuery.of(context).padding.bottom + 10,
            left: 16,
            right: 16),
        onTapLink: (text, href, title) async {
          openLink(href.toString());
        },
      ),
    );
  }
}

class CheckUpdateButton extends StatefulWidget {
  @override
  _CheckUpdateButtonState createState() => _CheckUpdateButtonState();
}

class _CheckUpdateButtonState extends State<CheckUpdateButton> {
  final String currentVersion = 'v2.6.6';
  final String githubRepo = 'PranshulGG/WeatherMaster';
  bool isChecking = false;

  Future<void> checkForUpdates() async {
    setState(() {
      isChecking = true;
    });

    final String releasesUrl =
        'https://api.github.com/repos/$githubRepo/releases';

    try {
      final response = await http.get(Uri.parse(releasesUrl));
      if (response.statusCode != 200) {
        throw Exception('Failed to fetch releases');
      }

      final List<dynamic> releases = jsonDecode(response.body);
      final latestStable = releases.firstWhere(
        (release) => release['prerelease'] == false,
        orElse: () => null,
      );

      await Future.delayed(Duration(seconds: 2));

      if (latestStable != null && latestStable['tag_name'] != currentVersion) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('new_version_available!'.tr()),
            behavior: SnackBarBehavior.floating,
          ),
        );

        await Future.delayed(Duration(seconds: 1));

        final url = 'https://github.com/$githubRepo/releases';
        openLink(url);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: Text('you_are_using_the_latest_version!'.tr()),
              behavior: SnackBarBehavior.floating),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content: Text('error_checking_for_updates'.tr()),
            behavior: SnackBarBehavior.floating),
      );
      print('Error: $e');
    }

    setState(() {
      isChecking = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return FilledButton.icon(
      onPressed: isChecking ? null : checkForUpdates,
      icon: Icon(
        Symbols.refresh,
        weight: 700,
      ),
      label:
          Text(currentVersion, style: TextStyle(fontWeight: FontWeight.w700)),
      style: ButtonStyle(
        padding: WidgetStateProperty.all(
          EdgeInsets.symmetric(horizontal: 12, vertical: 5),
        ),
        shape: WidgetStateProperty.all(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
                topLeft: Radius.circular(50),
                topRight: Radius.circular(3),
                bottomLeft: Radius.circular(50),
                bottomRight: Radius.circular(3)),
          ),
        ),
        minimumSize: WidgetStateProperty.all(Size(0, 30)),
        iconAlignment: IconAlignment.end,
        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
      ),
    );
  }
}

class ChangelogService {
  final String githubRepo = "PranshulGG/WeatherMaster";
  final Box _box = Hive.box('changelogs');

  Future<List<Map<String, dynamic>>> getChangelogs() async {
    final now = DateTime.now();
    final lastFetch = _box.get('lastFetch') as DateTime?;

    if (lastFetch != null &&
        now.difference(lastFetch) < const Duration(hours: 48)) {
      final cachedData = (_box.get('data', defaultValue: []) as List)
          .map((e) => Map<String, dynamic>.from(e as Map))
          .toList();

      await Future.delayed(const Duration(seconds: 2));
      return cachedData;
    }

    final url = Uri.parse("https://api.github.com/repos/$githubRepo/releases");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Failed to fetch releases");
    }

    final releases = jsonDecode(response.body) as List<dynamic>;

    final parsed = releases
        .where((r) => r['prerelease'] == false)
        .take(10)
        .map((r) => {
              "tag": r["tag_name"],
              "name": r["name"] ?? "",
              "body": r["body"] ?? "",
              "published_at": r["published_at"],
            })
        .toList();

    await _box.put('data', parsed);
    await _box.put('lastFetch', now);

    return parsed;
  }
}

class ChangelogSheet extends StatefulWidget {
  final ScrollController scrollController;
  const ChangelogSheet({super.key, required this.scrollController});

  @override
  State<ChangelogSheet> createState() => _ChangelogSheetState();
}

class _ChangelogSheetState extends State<ChangelogSheet> {
  final service = ChangelogService();
  late Future<List<Map<String, dynamic>>> _future;

  @override
  void initState() {
    super.initState();
    _future = service.getChangelogs();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: _future,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(
              child: ExpressiveLoadingIndicator(
            activeSize: 38,
            color: Theme.of(context).colorScheme.primary,
          ));
        }

        if (snapshot.hasError) {
          return Center(child: Text("Error loading changelog"));
        }

        final releases = snapshot.data as List<Map<String, dynamic>>;
        if (releases.isEmpty) {
          return const Center(child: Text("No changelogs available"));
        }

        final buffer = StringBuffer("");
        for (final release in releases) {
          buffer.writeln("# ${release['tag']}");
          buffer.writeln(release['body']);
          buffer.writeln("\n---\n");
        }

        final colorTheme = Theme.of(context).colorScheme;

        return ListView.builder(
          controller: widget.scrollController,
          itemCount: releases.length,
          itemBuilder: (context, index) {
            final release = releases[index];
            return Container(
              margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              padding: EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: colorTheme.surfaceContainerLowest,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(height: 6),
                  Row(spacing: 2.7, children: [
                    Container(
                      padding: const EdgeInsets.only(
                          left: 10, right: 10, bottom: 3, top: 3),
                      decoration: BoxDecoration(
                          color: colorTheme.primaryContainer,
                          borderRadius: BorderRadius.circular(50)),
                      child: Text(
                        release['tag'],
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color:
                              Theme.of(context).colorScheme.onPrimaryContainer,
                        ),
                      ),
                    ),
                    if (index == 0)
                      Container(
                        padding: const EdgeInsets.only(
                            left: 10, right: 10, bottom: 3, top: 3),
                        decoration: BoxDecoration(
                            color: colorTheme.tertiary,
                            borderRadius: BorderRadius.circular(8)),
                        child: Text(
                          "Latest",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Theme.of(context).colorScheme.onTertiary,
                          ),
                        ),
                      ),
                  ]),
                  SizedBox(height: 12),
                  MarkdownBody(
                    data: release['body'],
                    styleSheet: MarkdownStyleSheet.fromTheme(Theme.of(context))
                        .copyWith(
                      blockquote: TextStyle(
                        color: colorTheme.onSurfaceVariant,
                        fontStyle: FontStyle.italic,
                      ),
                      blockquoteDecoration: BoxDecoration(
                        color: Colors.transparent,
                        border: Border(
                            left: BorderSide(
                                color: colorTheme.outline, width: 4)),
                      ),
                    ),
                    onTapLink: (text, href, title) {
                      if (href != null) openLink(href);
                    },
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }
}

// CONTRIBUTORS

class Translator {
  final String name;
  final String fullName;
  final String role;

  Translator({required this.name, required this.fullName, required this.role});

  factory Translator.fromJson(Map<String, dynamic> json) {
    return Translator(
      name: json['username'] ?? '',
      fullName: json['fullName'] ?? '',
      role: json['role'] ?? '',
    );
  }
}

class TranslatorsDialog {
  static const String cacheKey = 'translatorsData';

  static Future<List<Translator>> fetchTranslators(
      {required String projectId, required String apiToken}) async {
    try {
      final prefs = await SharedPreferences.getInstance();

      final savedData = prefs.getString(cacheKey);
      if (savedData != null) {
        final cached = json.decode(savedData);
        final timestamp = cached['timestamp'] as int;
        final translatorsData = cached['translators'] as List<dynamic>;

        if (DateTime.now()
                .difference(DateTime.fromMillisecondsSinceEpoch(timestamp))
                .inHours <
            24) {
          print('Using cached translators data');

          return translatorsData.map((e) => Translator.fromJson(e)).toList();
        }
      }

      final response = await http.get(
        Uri.parse('https://api.crowdin.com/api/v2/projects/$projectId/members'),
        headers: {
          'Authorization': 'Bearer $apiToken',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode != 200) {
        throw Exception('Error fetching members: ${response.statusCode}');
      }

      final data = json.decode(response.body);
      final translators = (data['data'] as List<dynamic>)
          .where((member) => member['data']['role'] == 'translator')
          .map((member) => Translator(
                name: member['data']['username'] ?? '',
                fullName: member['data']['fullName'] ?? '',
                role: member['data']['role'] ?? '',
              ))
          .toList();

      final cacheData = json.encode({
        'timestamp': DateTime.now().millisecondsSinceEpoch,
        'translators': translators
            .map((t) =>
                {'username': t.name, 'fullName': t.fullName, 'role': t.role})
            .toList(),
      });

      await prefs.setString(cacheKey, cacheData);

      return translators;
    } catch (e) {
      print('Error fetching translators: $e');

      final prefs = await SharedPreferences.getInstance();
      final savedData = prefs.getString(cacheKey);
      if (savedData != null) {
        try {
          final cached = json.decode(savedData);
          final translatorsData = cached['translators'] as List<dynamic>;
          print('Using cached data fallback');
          return translatorsData.map((e) => Translator.fromJson(e)).toList();
        } catch (_) {}
      }

      return [];
    }
  }

  static Future<void> show(BuildContext context,
      {required String projectId, required String apiToken}) async {
    List<Translator> translators = [];
    bool isLoading = true;

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      showDragHandle: true,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            if (isLoading) {
              fetchTranslators(projectId: projectId, apiToken: apiToken)
                  .then((fetched) {
                setState(() {
                  translators = fetched;
                  isLoading = false;
                });
              });
            }

            final maxHeight = MediaQuery.of(context).size.height * 0.75;

            return Container(
              constraints: BoxConstraints(
                maxHeight: maxHeight,
              ),
              padding: EdgeInsets.only(
                bottom: MediaQuery.of(context).viewInsets.bottom,
                left: 16,
                right: 16,
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Expanded(
                    child: isLoading
                        ? const Center(child: CircularProgressIndicator())
                        : translators.isEmpty
                            ? const Center(
                                child: Text('No translators available.'))
                            : ListView.builder(
                                itemCount: translators.length,
                                itemBuilder: (context, index) {
                                  final t = translators[index];
                                  final displayName = (t.fullName.isNotEmpty
                                          ? t.fullName
                                          : t.name)
                                      .replaceAll(
                                          RegExp(r'[^\u0000-\uFFFF]'), '');
                                  final displayRole = (t.role.isNotEmpty
                                      ? t.role[0].toUpperCase() +
                                          t.role.substring(1)
                                      : '');
                                  return ListTile(
                                    leading: CircleAvatar(
                                      child: Text(
                                        displayName.isNotEmpty
                                            ? displayName[0]
                                            : '?',
                                      ),
                                    ),
                                    title: Text('$displayName (${t.name})'),
                                    subtitle: Text(displayRole),
                                  );
                                },
                              ),
                  ),
                  Divider(),
                  SizedBox(
                    width: double.infinity,
                    child: FilledButton(
                      onPressed: () => Navigator.pop(context),
                      child: Text('close'.tr()),
                    ),
                  ),
                  SizedBox(height: MediaQuery.of(context).padding.bottom + 10),
                ],
              ),
            );
          },
        );
      },
    );
  }
}
