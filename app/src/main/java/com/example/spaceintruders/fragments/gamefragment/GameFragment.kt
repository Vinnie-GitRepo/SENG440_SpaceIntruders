package com.example.spaceintruders.fragments.gamefragment

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spaceintruders.viewmodels.WifiViewModel
import kotlin.math.absoluteValue


/**
 * Fragment that represents game.
 */
class GameFragment : Fragment() {
    private val wifiViewModel: WifiViewModel by activityViewModels()
    private lateinit var gameSurfaceView: GameSurfaceView
    private lateinit var sensorManager: SensorManager

    private val gravityListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, p1: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            gameSurfaceView.tilt = ((event.values[0] / -9.81f) + 0.5f).coerceIn(0f, 1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(gravityListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val point = getScreenDimensions(requireActivity())
        gameSurfaceView = GameSurfaceView(requireContext(), point.x, point.y)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY).let { sensor ->
            sensorManager.registerListener(gravityListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
        return gameSurfaceView
    }

    /**
     * Gets dimensions of screen.
     * @return Point object containing width and height of screen.
     */
    private fun getScreenDimensions(activity: Activity): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val rect: Rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)
            Point(rect.width(), rect.height())
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    override fun onPause() {
        super.onPause()
        gameSurfaceView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameSurfaceView.resume()
    }
}