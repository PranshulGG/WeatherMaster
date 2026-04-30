package com.pranshulgg.weathermaster.feature.settings.language

data class Language(
    val name: String,
    val value: String,
    val nativeName: String
)

fun getLanguages(): List<Language> {
    return listOf(
        Language(name = "English", value = "en", nativeName = "English"),
        Language(name = "Arabic (Saudi Arabia)", value = "ar-SA", nativeName = "العربية"),
        Language(name = "Azerbaijani", value = "az-AZ", nativeName = "Azərbaycan dili"),
        Language(name = "Bulgarian", value = "bg-BG", nativeName = "Български"),
        Language(name = "Catalan", value = "ca-ES", nativeName = "Català"),
        Language(name = "Czech", value = "cs-CZ", nativeName = "Čeština"),
        Language(name = "German", value = "de-DE", nativeName = "Deutsch"),
        Language(name = "Greek", value = "el-GR", nativeName = "Ελληνικά"),
        Language(name = "Spanish", value = "es-ES", nativeName = "Español"),
        Language(name = "Persian", value = "fa-IR", nativeName = "فارسی"),
        Language(name = "Finnish", value = "fi-FI", nativeName = "Suomi"),
        Language(name = "Filipino", value = "fil-PH", nativeName = "Filipino"),
        Language(name = "French", value = "fr-FR", nativeName = "Français"),
        Language(name = "Hungarian", value = "hu-HU", nativeName = "Magyar"),
        Language(name = "Indonesian", value = "in-ID", nativeName = "Bahasa Indonesia"),
        Language(name = "Italian", value = "it-IT", nativeName = "Italiano"),
        Language(name = "Japanese", value = "ja-JP", nativeName = "日本語"),
        Language(name = "Korean", value = "ko-KR", nativeName = "한국어"),
        Language(name = "Dutch", value = "nl-NL", nativeName = "Nederlands"),
        Language(name = "Polish", value = "pl-PL", nativeName = "Polski"),
        Language(name = "Portuguese (Brazil)", value = "pt-BR", nativeName = "Português (Brasil)"),
        Language(
            name = "Portuguese (Portugal)",
            value = "pt-PT",
            nativeName = "Português (Portugal)"
        ),
        Language(name = "Romanian", value = "ro-RO", nativeName = "Română"),
        Language(name = "Russian", value = "ru-RU", nativeName = "Русский"),
        Language(name = "Slovenian", value = "sl-SI", nativeName = "Slovenščina"),
        Language(name = "Serbian (Cyrillic)", value = "sr-CS", nativeName = "Српски"),
        Language(name = "Serbian (Latin)", value = "sr-SP", nativeName = "Srpski"),
        Language(name = "Swedish", value = "sv-SE", nativeName = "Svenska"),
        Language(name = "Thai", value = "th-TH", nativeName = "ไทย"),
        Language(name = "Turkish", value = "tr-TR", nativeName = "Türkçe"),
        Language(name = "Ukrainian", value = "uk-UA", nativeName = "Українська"),
        Language(name = "Vietnamese", value = "vi-VN", nativeName = "Tiếng Việt"),
        Language(name = "Chinese (Simplified)", value = "zh-CN", nativeName = "简体中文"),
        Language(name = "Chinese (Traditional)", value = "zh-TW", nativeName = "繁體中文")
    )
}