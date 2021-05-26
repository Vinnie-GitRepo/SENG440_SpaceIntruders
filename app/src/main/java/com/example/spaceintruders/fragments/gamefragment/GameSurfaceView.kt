package com.example.spaceintruders.fragments.gamefragment

import android.content.Context
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.SurfaceView
import androidx.core.content.ContextCompat.getSystemService
import com.example.spaceintruders.gameentities.GameBackground
import com.example.spaceintruders.gameentities.PlayerEntity
import kotlin.math.absoluteValue

class GameSurfaceView(context: Context, val screenX: Int, val screenY: Int) : SurfaceView(context), Runnable {
    private lateinit var thread: Thread
    private var running: Boolean = false
    private val paint: Paint = Paint()
    val screenRatioX: Float = 0f
    val screenRatioY: Float = 0f
    val screenScaleX: Float = screenX / 1000f
    val screenScaleY: Float = screenY / 1000f
    var flat: Float = 0f

    private val background: GameBackground = GameBackground(screenX, screenY, resources)
    private val player: PlayerEntity = PlayerEntity(screenX, screenY, resources)

    override fun run() {
        while (running) {
            update()
            draw()
            sleep()
        }
    }

    fun update() {
        player.position = flat
        print(flat)
        if(flat < 0.05){
            player.position = 0.05f
        }
        if(flat > 0.95){
            player.position = 0.95f
        }
    }


    fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(background.background, background.x.toFloat(), background.y.toFloat(), paint)

//            canvas.drawBitmap(player.image, player.x.toFloat(), player.y.toFloat(), paint)
            player.draw(canvas)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun sleep() {
        try {
            Thread.sleep(10)
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        running = true
        thread = Thread(this)
        thread.start()

    }

    fun pause() {
        try {
            running = false
            thread.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
    }
}