package io.github.keep2iron.recyclergridview

/**
 * recyclerGridView layout condition
 */
abstract class Condition(block: (Condition.() -> Unit)? = null){

//    companion object {
//        const val ASPECT_RATIO_AUTO_SIZE = 0f
//    }

    var aspectRatio = 1f

    var maxPercentLayoutInParent = 1f

    var maxShowCount = 9

    init {
        block?.let {
            apply(block)
        }
    }

    internal fun aspectRatio(): Float {
        return aspectRatio
    }

    internal fun maxPercentLayoutInParent(): Float {
        return maxPercentLayoutInParent
    }

    internal fun maxShowCount(): Int = maxShowCount

    abstract fun weatherConditionApply(count: Int): Boolean

    abstract fun maxColumn(): Int

}