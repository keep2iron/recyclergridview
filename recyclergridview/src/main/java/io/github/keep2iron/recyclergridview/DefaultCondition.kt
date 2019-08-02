package io.github.keep2iron.recyclergridview

class DefaultCondition(block: (Condition.() -> Unit)? = null) : Condition(block) {

  override fun maxColumn(): Int = 3

  override fun weatherConditionApply(count: Int): Boolean {
    return true
  }

}