package com.example.spaceintruders.fragments.gamefragment

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.example.spaceintruders.R
import com.example.spaceintruders.gameentities.*
import com.example.spaceintruders.util.BulletCollection
import com.example.spaceintruders.viewmodels.GameViewModel
import com.example.spaceintruders.viewmodels.NearbyCommunication


class GameSurfaceView(context: Context, private val screenX: Int, private val screenY: Int, private val nearbyCommunication: NearbyCommunication, private val gameViewModel: GameViewModel) : SurfaceView(context) {
    private lateinit var thread: Thread
    private var running: Boolean = false
    private val paint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val typeface: Typeface = ResourcesCompat.getFont(context, R.font.orbitron)!!
// Typeface.createFromAsset(context.resources.assets, context.getpaR.font.orbitron)
    var tilt: Float = 0f
    private var canShootSmall = 0
    private var canShootbig = 0
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

    private fun vibrate(milliseconds: Long) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(milliseconds)
        }
    }

    private fun shootAcross() {
        if (canShootbig == 0) {
            canShootbig += 50
            vibrate(150)
            Thread {
                bullets.addBullet(BulletBigEntity(screenX, screenY, resources, tilt))
            }.start()
        }
    }

    private fun shoot() {
        if (canShootSmall == 0) {
            canShootSmall += 20
            Thread {
                vibrate(90)
                bullets.addBullet(BulletSmallEntity(screenX, screenY, resources, tilt))
            }.start()
        }
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
        if (canShootSmall > 0) canShootSmall -= 1
        if (canShootbig > 0) canShootbig -=1

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
                    vibrate(500)
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
            textPaint.color = Color.WHITE
            textPaint.textSize = 100f
            textPaint.typeface = typeface
            canvas.drawText(context.getString(R.string.lives).format((3 - gameViewModel.scoreVisitPlayer.value!!).toString()), screenX/10f, screenX/15f, textPaint)
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