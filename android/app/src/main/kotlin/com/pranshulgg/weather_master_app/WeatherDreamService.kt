package com.pranshulgg.weather_master_app

import android.service.dreams.DreamService
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint


class WeatherDreamService : DreamService() {
    private lateinit var flutterEngine: FlutterEngine
    private lateinit var flutterView: FlutterView

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true
        isScreenBright = true

        flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartEntrypoint(
                "onDreamServiceStarted"
            )
        )
        setContentView(R.layout.flutter_view)

        flutterView = findViewById(R.id.flutter_view);
        flutterView.attachToFlutterEngine(flutterEngine);
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        flutterEngine.lifecycleChannel.appIsResumed()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        flutterEngine.lifecycleChannel.appIsPaused()
    }

    override fun onDetachedFromWindow() {
        flutterView.detachFromFlutterEngine()
        //flutterEngine.lifecycleChannel.appIsDetached()
        super.onDetachedFromWindow()
    }

    //override fun onTrimMemory(level: Int) {
    //    super.onTrimMemory(level)
    //}
}


fun DartEntrypoint(dartEntrypointFunctionName: String): DartEntrypoint {
    val flutterLoader = FlutterInjector.instance().flutterLoader()

    if (!flutterLoader.initialized()) {
        throw AssertionError(
            "DartEntrypoints can only be created once a FlutterEngine is created."
        )
    }
    return DartEntrypoint(flutterLoader.findAppBundlePath(), dartEntrypointFunctionName)
}