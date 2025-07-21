import 'package:flutter/material.dart';
import 'open_links.dart';

Widget homeBottomBar(BuildContext context, isLight){
  return RepaintBoundary(
        child: Padding(
          padding: EdgeInsetsGeometry.only(bottom: MediaQuery.of(context).padding.bottom + 10, top: 16),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 4,
            children: [
              GestureDetector(
                onTap: () {
                  openLink("https://open-meteo.com/");
                },
                child: Text("Open-Meteo", style: TextStyle(color: isLight ? Colors.black : Colors.white, fontWeight: FontWeight.bold, fontSize: 18, decoration: TextDecoration.underline, decorationThickness: 3)),
                
              ),
              Text("CC BY 4.0", style: TextStyle(color: isLight ? Colors.black : Colors.white, fontWeight: FontWeight.bold, fontSize: 18)),
            ],
          )
        ),
      );
}