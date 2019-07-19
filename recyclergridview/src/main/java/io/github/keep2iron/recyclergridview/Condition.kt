package io.github.keep2iron.recyclergridview

/**
 * recyclerGridView layout condition
 */
abstract class Condition {

    /**
     * width / height ratio
     */
    open fun aspectRatio(): Float = 1f

    /**
     *
     */
    open fun maxPercentLayoutInParent(): Float = 1f

    open fun maxShowCount(): Int = 9

    abstract fun weatherConditionApply(count: Int): Boolean

    abstract fun maxColumn(): Int

}