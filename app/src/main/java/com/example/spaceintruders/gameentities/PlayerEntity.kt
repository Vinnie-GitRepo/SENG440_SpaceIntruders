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

class PlayerEntity(private val screenX: Int, private val screenY: Int, res: Resources, context: Context) : Drawable() {
    private var height: Int = 0
    private var width: Int = 0
    private val paint: Paint = Paint()
    private var image: Bitmap = BitmapFactory.decodeResource(res, R.drawable.player_image)
    private var color: Int
    var position: Float = 0f

    init {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        color = sharedPref.getInt("colour", Color.rgb(255, 255, 255))
        colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)

        width = screenX/10
        height = screenX/10

        image = Bitmap.createScaledBitmap(image, width, height, false)
    }

    override fun draw(canvas: Canvas) {
        mutate()
        canvas.drawBitmap(image, (screenX.toFloat()*position)-(width/2), screenY.toFloat()-height, paint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }


}