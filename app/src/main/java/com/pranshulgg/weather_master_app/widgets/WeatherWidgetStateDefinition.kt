package com.pranshulgg.weather_master_app.widgets

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object WeatherWidgetStateDefinition :
    GlanceStateDefinition<WeatherWidgetStateJson> {

    private const val FILE_NAME =
        "weather_widget_state"

    private val Context.datastore by dataStore(
        fileName = FILE_NAME,
        serializer = WeatherWidgetSerializer
    )

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WeatherWidgetStateJson> {

        return context.datastore
    }

    override fun getLocation(
        context: Context,
        fileKey: String
    ): File {

        return context.dataStoreFile(FILE_NAME)
    }

    object WeatherWidgetSerializer :
        Serializer<WeatherWidgetStateJson> {

        override val defaultValue =
            WeatherWidgetStateJson()

        override suspend fun readFrom(
            input: InputStream
        ): WeatherWidgetStateJson {

            return try {

                Json.decodeFromString(
                    WeatherWidgetStateJson.serializer(),
                    input.readBytes().decodeToString()
                )

            } catch (e: Exception) {

                WeatherWidgetStateJson()
            }
        }

        override suspend fun writeTo(
            t: WeatherWidgetStateJson,
            output: OutputStream
        ) {

            withContext(Dispatchers.IO) {
                output.write(
                    Json.encodeToString(
                        WeatherWidgetStateJson.serializer(),
                        t
                    ).encodeToByteArray()
                )
            }
        }
    }
}