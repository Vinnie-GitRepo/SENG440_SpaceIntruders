package com.example.spaceintruders.fragments.gamefragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import com.example.spaceintruders.gameentities.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import java.util.*

class GameSurfaceView(context: Context, val screenX: Int, val screenY: Int) : SurfaceView(context) {
    private lateinit var thread: Thread
    private var running: Boolean = false
    private val paint: Paint = Paint()
    val screenRatioX: Float = 0f
    val screenRatioY: Float = 0f
    val screenScaleX: Float = screenX / 1000f
    val screenScaleY: Float = screenY / 1000f
    var tilt: Float = 0f
    private val bullets: BulletCollection = BulletCollection()

    private val background: GameBackground = GameBackground(screenX, screenY, resources)
    private val player: PlayerEntity = PlayerEntity(screenX, screenY, resources)

    init {
        setWillNotDraw(false)
    }

    private val runner = Runnable {
        while (running) {
            update()
            sleep()
        }
    }

    fun shoot() {
        Thread(Runnable {
            bullets.addBullet(BulletSmallEntity(screenX, screenY, resources, tilt))
        }).start()
    }

    fun handleDown(event: MotionEvent?) {
        if (event?.actionMasked == MotionEvent.ACTION_DOWN) {
            shoot()
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        handleDown(event)
        return true;
    }

    private fun update() {
        player.position = tilt
        for (bullet in bullets.getBulletCopy()) {
            if (bullet.position < 1) {
                bullet.updatePosition()
            } else {
                bullets.deleteBullet(bullet)
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(background.background, background.x.toFloat(), background.y.toFloat(), paint)
            player.draw(canvas)
            for (bullet in bullets.getBulletCopy()) {
                bullet.draw(canvas)
            }

            holder.unlockCanvasAndPost(canvas)
            invalidate()
        }
    }

    private fun sleep() {
        try {
            Thread.sleep(10)
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        running = true
        thread = Thread(runner)
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