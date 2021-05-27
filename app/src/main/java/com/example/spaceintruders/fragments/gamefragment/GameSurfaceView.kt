package com.example.spaceintruders.fragments.gamefragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.fragment.app.activityViewModels
import com.example.spaceintruders.gameentities.*
import com.example.spaceintruders.util.BulletCollection
import com.example.spaceintruders.viewmodels.GameViewModel
import com.example.spaceintruders.viewmodels.WifiViewModel

class GameSurfaceView(context: Context, val screenX: Int, val screenY: Int, private val wifiModel: WifiViewModel, private val gameViewModel: GameViewModel) : SurfaceView(context) {
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
    private val player: PlayerEntity = PlayerEntity(screenX, screenY, resources, context)

    /**
     * Runner that updates the game variables.
     */
    private val runner = Runnable {
        while (running) {
            update()
            sleep()
        }
    }

    private val flingListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            // Returns true if the fling was fast enough.
            if (velocityY < -4000) {
                return true
            }
            return false
        }
    }
    private val flingDetector = GestureDetector(context, flingListener)

    /**
     * Sets view to automatically draw.
     */
    init {
        setWillNotDraw(false)
    }

    private fun shootAcross() {
        Thread {
            bullets.addBullet(BulletBigEntity(screenX, screenY, resources, tilt))
        }.start()
    }

    private fun shoot() {
        Thread {
            bullets.addBullet(BulletSmallEntity(screenX, screenY, resources, tilt))
        }.start()
    }

    fun enemyShoot(position: Float) {
        Thread {
            bullets.addBullet(BulletEnemy(screenX, screenY, resources, position))
        }.start()
    }

    private fun handleDown(event: MotionEvent?) {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            shoot()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (flingDetector.onTouchEvent(event)) {
            shootAcross()
        } else {
            handleDown(event)
        }
        return true
    }

    private fun update() {
        player.position = tilt
        for (bullet in bullets.getBulletCopy()) {
            if (!bullet.travelComplete()) {
                bullet.updatePosition()
            } else {
                if (bullet is BulletEnemy) {
                    Log.d("GameView: Update", "Enemy scored")
                    gameViewModel.enemyScored()
                } else if (bullet is BulletBigEntity) {
                    Log.d("GameView: Update", "Big bullet sending")
                    wifiModel.sendBullet(bullet)
                }
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