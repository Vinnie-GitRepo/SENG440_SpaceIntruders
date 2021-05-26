package com.example.spaceintruders.gameentities

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.util.Log
import com.example.spaceintruders.R

class PlayerEntity(private val screenX: Int, private val screenY: Int, res: Resources) : Drawable() {
    private var height: Int = 0
    private var width: Int = 0
    private val paint: Paint = Paint()
    private var image: Bitmap = BitmapFactory.decodeResource(res, R.drawable.player_image)

    var position: Float = 0f

    init {
        width = screenX/10
        height = screenX/10

        image = Bitmap.createScaledBitmap(image, width, height, false)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, (screenX.toFloat()*position)-(width/2), screenY.toFloat()-height, paint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }


}