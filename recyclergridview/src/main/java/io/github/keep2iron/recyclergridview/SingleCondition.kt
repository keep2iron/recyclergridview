package io.github.keep2iron.recyclergridview

open class SingleCondition : Condition() {

    override fun maxColumn(): Int = 1

    override fun weatherConditionApply(count: Int): Boolean = count == 1

    override fun maxPercentLayoutInParent(): Float = 0.75f

    override fun aspectRatio(): Float = 1f

    override fun maxShowCount(): Int = 1
}