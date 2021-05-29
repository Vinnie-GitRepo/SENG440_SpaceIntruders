package com.example.spaceintruders.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

object AppUtil {
    /**
     * Gets attr colours
     * @param context
     * @param attrColor attribute resource to access
     * @param typedValue typed value to output into
     * @param resolveRefs whether to walk references
     * @return the retrieved colour
     */
    @ColorInt
    fun getColorFromAttr(
        context: Context,
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        context.theme?.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }
}