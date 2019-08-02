package io.github.keep2iron.recyclergridview

class TwoXTwoCondition(
  block: Condition.() -> Unit = {
    maxPercentLayoutInParent = 0.85f
  }
) : Condition(block) {

  override fun weatherConditionApply(count: Int): Boolean = count == 4

  override fun maxColumn(): Int = 2

}