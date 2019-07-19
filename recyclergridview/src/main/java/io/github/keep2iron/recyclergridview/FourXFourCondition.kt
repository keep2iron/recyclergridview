package io.github.keep2iron.recyclergridview

class FourXFourCondition : Condition() {

    override fun maxPercentLayoutInParent(): Float = 0.85f

    override fun weatherConditionApply(count: Int): Boolean = count == 4

    override fun maxColumn(): Int = 2

    override fun aspectRatio(): Float = 2f
}