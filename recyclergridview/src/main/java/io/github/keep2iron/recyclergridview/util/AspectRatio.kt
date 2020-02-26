package io.github.keep2iron.recyclergridview.util

import android.view.View
import java.lang.IllegalArgumentException
import kotlin.math.roundToInt

class AspectRatio private constructor(
    val ratio: Float,
    val height: Float,
    val mode: Int
) {

    companion object {
        /**
         * 宽高比
         */
        internal const val MODE_RATIO = 1

        /**
         * 包裹内容
         */
        internal const val MODE_WRAP = 2

        /**
         * 明确多少size
         */
        internal const val MODE_EXACTLY = 3

        fun ratio(ratio: Float): AspectRatio {
            return AspectRatio(
                ratio,
                0f,
                MODE_RATIO
            )
        }

        fun wrap(): AspectRatio {
            return AspectRatio(
                0f,
                0f,
                MODE_WRAP
            )
        }

        fun exactly(height: Float): AspectRatio {
            return AspectRatio(
                0f,
                height,
                MODE_EXACTLY
            )
        }
    }

    fun calculateMeasureSpec(width:Int):Int{
        return when(mode){
            MODE_RATIO->{
                (width / ratio).roundToInt()
            }
            MODE_WRAP->{
                0
            }
            MODE_EXACTLY->{
                height.roundToInt()
            }
            else->{
                throw IllegalArgumentException("mode is illegal.")
            }
        }
    }

}