package io.github.keep2iron.recyclergridview

import io.github.keep2iron.recyclergridview.util.AspectRatio
import io.github.keep2iron.recyclergridview.util.Range


/**
 * recyclerGridView layout condition
 */
open class Condition private constructor(
    private val itemRange: Range<Int>,
    val maxColumn: Int,
    val itemAspectRatio: AspectRatio = AspectRatio.ratio(1f),
    val maxShowCount: Int = MAX_SHOW_COUNT,
    val maxPercentLayoutInParent: Float = 1f
) {

    init {
        check(itemRange.upper <= maxShowCount) { "itemRange max value > maxShowCount" }
    }

    internal fun weatherConditionApply(count: Int): Boolean {
        return itemRange.contains(count)
    }

    companion object {
        const val MAX_SHOW_COUNT = 9

        fun createCondition(
            itemRange: Range<Int>,
            maxColumn: Int,
            itemAspectRatio: AspectRatio = AspectRatio.ratio(1f),
            maxShowCount: Int = MAX_SHOW_COUNT,
            maxPercentLayoutInParent: Float = 1f
        ): Condition {
            return Condition(
                itemRange,
                maxColumn,
                itemAspectRatio,
                maxShowCount,
                maxPercentLayoutInParent
            )
        }

        fun createSingleCondition(
            itemRange: Range<Int> = Range(1, 1),
            maxColumn: Int = 1,
            itemAspectRatio: AspectRatio = AspectRatio.ratio(1f),
            maxShowCount: Int = 1,
            maxPercentLayoutInParent: Float = 0.85f
        ): Condition {
          return Condition(
            itemRange,
            maxColumn,
            itemAspectRatio,
            maxShowCount,
            maxPercentLayoutInParent
          )
        }

        fun createDefaultCondition(
            itemRange: Range<Int> = Range(0, MAX_SHOW_COUNT),
            maxColumn: Int = 3,
            itemAspectRatio: AspectRatio = AspectRatio.ratio(1f),
            maxShowCount: Int = MAX_SHOW_COUNT,
            maxPercentLayoutInParent: Float = 1f
        ): Condition {
            return Condition(
                itemRange,
                maxColumn,
                itemAspectRatio,
                maxShowCount,
                maxPercentLayoutInParent
            )
        }

        fun create2X2Condition(
            itemRange: Range<Int> = Range(4, 4),
            maxColumn: Int = 2,
            itemAspectRatio: AspectRatio = AspectRatio.ratio(1f),
            maxShowCount: Int = MAX_SHOW_COUNT,
            maxPercentLayoutInParent: Float = 0.8f
        ): Condition {
            return Condition(
                itemRange,
                maxColumn,
                itemAspectRatio,
                maxShowCount,
                maxPercentLayoutInParent
            )
        }
    }
}