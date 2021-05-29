package com.example.spaceintruders.fragments.gamefragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.spaceintruders.gameentities.*
import com.example.spaceintruders.services.NearbyCommunication
import com.example.spaceintruders.util.BulletCollection
import com.example.spaceintruders.viewmodels.GameViewModel
import com.example.spaceintruders.viewmodels.WifiViewModel

class GameSurfaceView(context: Context, private val screenX: Int, private val screenY: Int, private val nearbyCommunication: NearbyCommunication, private val gameViewModel: GameViewModel) : SurfaceView(context) {
    private lateinit var thread: Thread
    private var running: Boolean = false
    private val paint: Paint = Paint()

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
                // This is to check if enemy bullet has collided with a friendly bullet.
                if (bullet is BulletEnemy) {
                    for (otherBullet in bullets.getBulletCopy()) {
                        if (otherBullet is BulletSmallEntity) {
                            Log.d("other bullet", "here")
                            if (bullet.positionX-0.1 < otherBullet.positionX &&
                                otherBullet.positionX < bullet.positionX+0.1 &&
                                    otherBullet.positionY < bullet.positionY) {
                                bullets.deleteBullet(bullet)
                            }
                        }
                    }
                }
                bullet.updatePosition()
            } else {
                if (bullet is BulletEnemy) {
                    Log.d("GameView: Update", "Enemy scored")
                    gameViewModel.enemyScored()
                } else if (bullet is BulletBigEntity) {
                    Log.d("GameView: Update", "Big bullet sending")
                    nearbyCommunication.sendBullet(context, bullet)
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