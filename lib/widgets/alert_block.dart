import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
Widget alertCard(Map<String, dynamic> data, BuildContext context) {
  List alerts = data['alert'] ?? [];

  if (alerts.isEmpty) {
    return const Text("No alerts available.");
  }

  final alert = alerts.first;

        return GestureDetector(

        
        child: Container(
    padding: const EdgeInsets.all(12),
    margin: const EdgeInsets.fromLTRB(12, 0, 12, 0),
    decoration: BoxDecoration(
      color: Theme.of(context).colorScheme.errorContainer,
      borderRadius: BorderRadius.circular(18),
    ),
    constraints: BoxConstraints(
      minHeight: 65
    ),
    child: IntrinsicHeight(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center, 
        children: [
          Container(
            width: 46,
            height: 46,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.error,
              borderRadius: BorderRadius.circular(50),
              
            ),
            child: Center(
              child: Icon(
                Icons.warning_amber,
                color: Theme.of(context).colorScheme.onError,
              ),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              alert['headline'] ?? 'No headline',
              style: TextStyle(
                fontSize: 14.5,
                color: Theme.of(context).colorScheme.onErrorContainer,
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          ),
          Icon(
            Icons.chevron_right_rounded,
            color: Theme.of(context).colorScheme.onSurface,
          ),
        ],
      ),
    ),
    ),
    onTap: () {
      Navigator.of(context).push(
      MaterialPageRoute(builder: (_) =>  AlertsPage(data: data,)),
    );
    },
  );
}

class AlertsPage extends StatelessWidget {
  final Map<String, dynamic> data;

  const AlertsPage({super.key, required this.data});

  @override
  Widget build(BuildContext context) {
      List alerts = data['alert'] ?? [];

  return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            titleSpacing: 0,
            automaticallyImplyLeading: false,
            flexibleSpace: FlexibleSpaceBar(
            title: Text("weather_alerts".tr()),
              expandedTitleScale: 1.3,
              titlePadding: EdgeInsets.all(16),
            ),
            backgroundColor: Theme.of(context).colorScheme.surface,
            expandedHeight: 100,
            scrolledUnderElevation: 1,
            actions: [
              IconButton(onPressed: () {Navigator.pop(context);}, icon: Icon(Icons.close, weight: 600,)),
              SizedBox(width: 5)
            ],
          ),

           SliverToBoxAdapter(
            child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Opacity(
          opacity: 0.7,
                     child: Padding(padding: EdgeInsets.only(bottom: 10, left: 16, right: 16),
                     child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                       children: [
                        Container(
                          padding: EdgeInsets.only(top: 2),
                          child: Icon(Icons.info_outline, color: Theme.of(context).colorScheme.onSurfaceVariant, size: 17,),
                        ),
                        SizedBox(width: 5,),
                        Expanded(
                        child: Text(
                          "Due to the API being in beta, alerts from other regions may occasionally appear",
                          style: TextStyle(
                            color: Theme.of(context).colorScheme.onSurfaceVariant
                          ),
                        ),
                        ),
                       ],
                      ),
                      ),  
                    ),
          if (alerts.isNotEmpty)
          ...alerts.map<Widget>((alert) {
            return Container(
              margin: const EdgeInsets.symmetric(vertical: 6.0, horizontal: 12),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainerLow,
                borderRadius: BorderRadius.circular(18),
              ),
              child: Padding(
                padding: const EdgeInsets.all(12.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      alert['headline'] ?? 'No headline',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Theme.of(context).colorScheme.error,
                      ),
                    ),
                    Divider(),
                    const SizedBox(height: 8),
                    Text(
                      alert['desc']?.replaceAll('\n', '\n') ?? '',
                      style: const TextStyle(fontSize: 14),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      alert['instruction']?.replaceAll('\n', '\n') ?? '',
                      style: const TextStyle(
                        fontSize: 14,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Divider(),
                    Text(
                      'Effective: ${formatDate(alert['effective'])}',
                      style: TextStyle(fontSize: 15, color: Theme.of(context).colorScheme.primary),
                    ),
                    Text(
                      'Expires: ${formatDate(alert['expires'])}',
                      style:  TextStyle(fontSize: 15, color: Theme.of(context).colorScheme.primary),
                    ),
                  ],
                ),
              ),
            );
          }).toList(),

        SizedBox(height: MediaQuery.of(context).padding.bottom + 10,)
        ],
      ),
           ),
        ]
      )
    );




  }
}

DateFormat dateFormat = DateFormat('EEEE, MMM d • h:mm a');

String formatDate(String dateStr) {
  try {
    DateTime dt = DateTime.parse(dateStr).toLocal();
    return dateFormat.format(dt);
  } catch (_) {
    return dateStr; 
  }
}