enum LayoutBlockType {
  rain,
  insights,
  summary,
  hourly,
  daily,
  conditions,
  pollen,
}

class LayoutBlockConfig {
  final LayoutBlockType type;
  bool isVisible;

  LayoutBlockConfig({required this.type, this.isVisible = true});

  Map<String, dynamic> toJson() => {
        'type': type.name,
        'isVisible': isVisible,
      };

  factory LayoutBlockConfig.fromJson(Map<String, dynamic> json) {
    return LayoutBlockConfig(
      type: LayoutBlockType.values.firstWhere((e) => e.name == json['type']),
      isVisible: json['isVisible'] ?? true,
    );
  }
}
