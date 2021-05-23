package com.example.spaceintruders.fragments.gamefragment

import android.content.Context
import android.graphics.Paint
import android.view.SurfaceView
import com.example.spaceintruders.gameentities.GameBackground

class GameSurfaceView(context: Context, screenX: Int, screenY: Int) : SurfaceView(context), Runnable {
    private lateinit var thread: Thread
    private var running: Boolean = false
    private var paint: Paint = Paint()
    private var screenRatioX: Float = 0f
    private var screenRatioY: Float = 0f

    private var backgroud: GameBackground = GameBackground(screenX, screenY, resources)

    override fun run() {
        while (running) {
            update()
            draw()
            sleep()
        }
    }

    fun update() {

    }

    fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(backgroud.background, backgroud.x.toFloat(), backgroud.y.toFloat(), paint)

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