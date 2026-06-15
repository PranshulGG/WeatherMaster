package com.pranshulgg.weather_master_app.core.utils.locale

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

data class Language(
    val name: String,
    val value: String,
    val nativeName: String,
    val code: String
)


fun getAppLocalLocales(): List<Language> {
    return listOf(
        Language(name = "System", value = "sys", nativeName = "Your device language", code = "SYS"),
        Language(name = "English", value = "en", nativeName = "English", code = "US"),
        Language(
            name = "Arabic (Saudi Arabia)",
            value = "ar-SA",
            nativeName = "العربية",
            code = "AR"
        ),
        Language(
            name = "Azerbaijani",
            value = "az-AZ",
            nativeName = "Azərbaycan dili",
            code = "AZ"
        ),
        Language(name = "Bulgarian", value = "bg-BG", nativeName = "Български", code = "BG"),
        Language(name = "Catalan", value = "ca-ES", nativeName = "Català", code = "ES"),
        Language(name = "Czech", value = "cs-CZ", nativeName = "Čeština", code = "CZ"),
        Language(name = "German", value = "de-DE", nativeName = "Deutsch", code = "DE"),
        Language(name = "Greek", value = "el-GR", nativeName = "Ελληνικά", code = "GR"),
        Language(name = "Spanish", value = "es-ES", nativeName = "Español", code = "ES"),
        Language(name = "Persian", value = "fa-IR", nativeName = "فارسی", code = "IR"),
        Language(name = "Finnish", value = "fi-FI", nativeName = "Suomi", code = "FI"),
        Language(name = "Filipino", value = "fil-PH", nativeName = "Filipino", code = "PH"),
        Language(name = "French", value = "fr-FR", nativeName = "Français", code = "FR"),
        Language(name = "Hungarian", value = "hu-HU", nativeName = "Magyar", code = "HU"),
        Language(
            name = "Indonesian",
            value = "id-ID",
            nativeName = "Bahasa Indonesia",
            code = "ID"
        ),
        Language(name = "Italian", value = "it-IT", nativeName = "Italiano", code = "IT"),
        Language(name = "Japanese", value = "ja-JP", nativeName = "日本語", code = "JP"),
        Language(name = "Korean", value = "ko-KR", nativeName = "한국어", code = "KR"),
        Language(name = "Dutch", value = "nl-NL", nativeName = "Nederlands", code = "NL"),
        Language(name = "Polish", value = "pl-PL", nativeName = "Polski", code = "PL"),
        Language(
            name = "Portuguese (Brazil)",
            value = "pt-BR",
            nativeName = "Português (Brasil)", code = "BR"
        ),
        Language(
            name = "Portuguese (Portugal)",
            value = "pt-PT",
            nativeName = "Português (Portugal)", code = "PT"
        ),
        Language(name = "Romanian", value = "ro-RO", nativeName = "Română", code = "RO"),
        Language(name = "Russian", value = "ru-RU", nativeName = "Русский", code = "RU"),
        Language(name = "Slovenian", value = "sl-SI", nativeName = "Slovenščina", code = "SI"),
        Language(
            name = "Serbian (Cyrillic)",
            value = "sr-CS",
            nativeName = "Српски",
            code = "CS"
        ),
        Language(name = "Serbian (Latin)", value = "sr-SP", nativeName = "Srpski", code = "SP"),
        Language(name = "Swedish", value = "sv-SE", nativeName = "Svenska", code = "SE"),
        Language(name = "Thai", value = "th-TH", nativeName = "ไทย", code = "TH"),
        Language(name = "Turkish", value = "tr-TR", nativeName = "Türkçe", code = "TR"),
        Language(name = "Ukrainian", value = "uk-UA", nativeName = "Українська", code = "UA"),
        Language(name = "Vietnamese", value = "vi-VN", nativeName = "Tiếng Việt", code = "VN"),
        Language(
            name = "Chinese (Simplified)",
            value = "zh-CN",
            nativeName = "简体中文",
            code = "CN"
        ),
        Language(
            name = "Chinese (Traditional)",
            value = "zh-TW",
            nativeName = "繁體中文",
            code = "TW"
        )
    )
}

fun getCurrentAppLocale(): Locale {
    val locale = AppCompatDelegate.getApplicationLocales()[0]
    return locale ?: Locale.getDefault()
}

