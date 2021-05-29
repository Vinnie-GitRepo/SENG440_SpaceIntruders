package com.example.spaceintruders.fragments.gamefragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import com.example.spaceintruders.services.NearbyCommunication
import com.example.spaceintruders.viewmodels.GameViewModel
import com.example.spaceintruders.viewmodels.WifiViewModel
import java.lang.NumberFormatException
import kotlin.math.absoluteValue


/**
 * Fragment that represents game.
 */
class GameFragment : Fragment() {
    private val nearbyCommunication: NearbyCommunication by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()

    private lateinit var gameSurfaceView: GameSurfaceView
    private lateinit var sensorManager: SensorManager

    init {
    }

    private val gravityListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, p1: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            gameSurfaceView.tilt = ((event.values[0] / -9.81f) + 0.5f).coerceIn(0f, 1f)
        }
    }

    private fun parseInstruction(instruction: String) {
        Log.d("Instruction parser", "Instruction: '${instruction}'")
        if (instruction.startsWith("bullet")) {
            try {
                Log.i("Instruction parser", "Attempting to parse...")
                val number = instruction.removePrefix("bullet")
                Log.i("Instruction parser", number)
                gameSurfaceView.enemyShoot(number.toFloat())
                nearbyCommunication.resetInstruction()
            } catch (e : NumberFormatException) {
                Log.e("Instruction parser", "Failed to parse: $e")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(gravityListener)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create game view
        val point = getScreenDimensions(requireActivity())
        gameSurfaceView = GameSurfaceView(requireContext(), point.x, point.y, nearbyCommunication, gameViewModel)

        // Create sensor manager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY).let { sensor ->
            sensorManager.registerListener(gravityListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
        // Locks activity to portrait for this activity.
        // It must be unlocked when leaving this fragment.
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nearbyCommunication.instruction.observe(viewLifecycleOwner) {parseInstruction(it)}
        return gameSurfaceView
    }

    override fun onPause() {
        super.onPause()
        gameSurfaceView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameSurfaceView.resume()
    }

    /**
     * Gets dimensions of screen.
     * @return Point object containing width and height of screen.
     */
    private fun getScreenDimensions(activity: Activity): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)
            Point(rect.width(), rect.height())
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }
}