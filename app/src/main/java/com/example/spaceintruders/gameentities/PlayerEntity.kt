package com.example.spaceintruders.gameentities

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.spaceintruders.R
import com.example.spaceintruders.activities.SettingsActivity

class PlayerEntity(private val screenX: Int, private val screenY: Int, res: Resources) : Drawable() {
    private var x: Int = 0
    private val y: Int = screenY
    private var height: Int = 0
    private var width: Int = 0
    private val paint: Paint = Paint()

    var position: Float = 0f

    var image: Bitmap = BitmapFactory.decodeResource(res, R.drawable.player_image)

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