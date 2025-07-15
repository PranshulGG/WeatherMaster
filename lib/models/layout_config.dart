class LayoutItem {
  final String id;
  final String label;
  bool isVisible;

  LayoutItem({
    required this.id,
    required this.label,
    required this.isVisible,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'label': label,
        'isVisible': isVisible,
      };

  static LayoutItem fromJson(Map<String, dynamic> json) => LayoutItem(
        id: json['id'],
        label: json['label'],
        isVisible: json['isVisible'],
      );
}
