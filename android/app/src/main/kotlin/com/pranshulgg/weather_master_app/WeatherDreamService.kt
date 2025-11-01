package com.pranshulgg.weather_master_app

import android.service.dreams.DreamService
import android.view.ViewGroup
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint


class WeatherDreamService : DreamService() {
    companion object {
        private var flutterEngine: FlutterEngine? = null
    }
    
    private var flutterView: FlutterView? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Allow click to propagate to the FlutterView
        isInteractive = true

        // Make the window fullscreen
        isFullscreen = true

        // TODO: figure out if this is required or not
        isScreenBright = true

        // TODO: decide if this should use applicationContext or this
        val engine = flutterEngine ?: FlutterEngine(applicationContext).also {
            it.dartExecutor.executeDartEntrypoint(
                DartEntrypoint(
                    dartEntrypointFunctionName = "onDreamServiceStarted"
                )
            )
        }


        setContentView(R.layout.flutter_view)
        flutterView = findViewById<FlutterView>(R.id.flutter_view).also {
            it.attachToFlutterEngine(engine)
        }

        //flutterView = FlutterView(this).also {
        //    it.layoutParams = ViewGroup.LayoutParams(
        //        ViewGroup.LayoutParams.MATCH_PARENT,
        //        ViewGroup.LayoutParams.MATCH_PARENT
        //    )
        //    setContentView(it)
        //    it.attachToFlutterEngine(engine)
        //}
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        flutterEngine?.lifecycleChannel?.appIsResumed()
    }

    override fun onDreamingStopped() {
        //flutterEngine?.lifecycleChannel?.appIsInactive()
        flutterEngine?.lifecycleChannel?.appIsPaused()
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        flutterView?.let {
            it.detachFromFlutterEngine()
            flutterView = null
        }
        super.onDetachedFromWindow()
    }
}

fun DartEntrypoint(
    pathToBundle: String? = null,
    dartEntrypointLibrary: String? = null,
    dartEntrypointFunctionName: String = "main"
): DartEntrypoint {
    val pathToBundle = pathToBundle ?: FlutterInjector.instance().flutterLoader().let {
        if (!it.initialized()) {
            throw AssertionError(
                "DartEntrypoints can only be created once a FlutterEngine is created."
            )
        }
        it.findAppBundlePath()
    }
    return dartEntrypointLibrary?.let {
        DartEntrypoint(
            pathToBundle,
            it,
            dartEntrypointFunctionName
        )
    } ?: DartEntrypoint(pathToBundle, dartEntrypointFunctionName)
}