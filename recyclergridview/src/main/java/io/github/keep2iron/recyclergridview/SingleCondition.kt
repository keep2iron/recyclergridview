package io.github.keep2iron.recyclergridview

open class SingleCondition(
  block: (Condition.() -> Unit) = {
    maxPercentLayoutInParent = 0.8f
  }
) : Condition(block) {

  override fun maxColumn(): Int = 1

  override fun weatherConditionApply(count: Int): Boolean = count == 1

}