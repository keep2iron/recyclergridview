package io.github.keep2iron.recyclergridview

class DefaultCondition : Condition() {

    override fun maxColumn(): Int = 3

    override fun weatherConditionApply(count: Int): Boolean {
        return true
    }

    override fun maxShowCount(): Int = 9

}