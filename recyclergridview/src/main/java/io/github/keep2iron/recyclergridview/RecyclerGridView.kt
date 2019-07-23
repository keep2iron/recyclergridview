package io.github.keep2iron.recyclergridview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.math.roundToInt

class RecyclerGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "RecyclerGridView"
    }

    //grid space
    private var gridSpacing = 5

    private var gridWidth = 0

    private var gridHeight = 0

    private var conditions = LinkedList<Condition>()

    //set from condition
    private var maxColumn = -1
    //set from condition
    private var maxImageCount = -1

    private var adapter: Adapter? = null

    private var viewPool: RecyclerView.RecycledViewPool? = null

    private var viewHolders = mutableListOf<RecyclerView.ViewHolder>()

    open class InternalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    abstract class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(viewParent: ViewGroup, viewType: Int): InternalViewHolder {
            return InternalViewHolder(onCreateView(viewParent, viewType))
        }

        abstract fun onCreateView(viewParent: ViewGroup, viewType: Int): View

        override fun getItemViewType(position: Int): Int = 1024
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (adapter == null) {
            Log.w(TAG, "adapter is null, onMeasure will skip.")
            return
        }

        val totalWidth: Int
        val totalHeight: Int

        var measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val notEmptyAdapter = adapter!!

        val condition = conditions.first {
            it.weatherConditionApply(notEmptyAdapter.itemCount)
        }

        when {
            (notEmptyAdapter.itemCount == 1 || condition.maxShowCount() == 1) -> {
                totalWidth = (condition.maxPercentLayoutInParent() * measureWidth).roundToInt()
                totalHeight = (totalWidth / condition.aspectRatio()).roundToInt()
                gridWidth = totalWidth

                gridHeight = totalHeight
            }
            //todo 下一个版本做进行自动适配宽高的版本
//            (notEmptyAdapter.itemCount == 1 || condition.maxShowCount() == 1) && condition.aspectRatio == ASPECT_RATIO_AUTO_SIZE -> {
//                val childLayoutParams = getChildAt(0).layoutParams
//                val tempWidth = if (childLayoutParams.width > measureWidth) measureWidth else childLayoutParams.width
//                totalWidth = (tempWidth * condition.maxPercentLayoutInParent()).roundToInt()
//                totalHeight = (totalWidth / condition.aspectRatio()).roundToInt()
//
//                childLayoutParams.width = totalWidth
//                childLayoutParams.height = totalHeight
//            }
            else -> {
                measureWidth = (condition.maxPercentLayoutInParent() * measureWidth).roundToInt()

                val itemWidth = ((measureWidth - (maxColumn - 1) * dp(gridSpacing)) / (maxColumn * 1f)).roundToInt()
                val itemHeight = (itemWidth / condition.aspectRatio()).roundToInt()
                val itemCount = if (notEmptyAdapter.itemCount >= maxImageCount) {
                    maxImageCount
                } else {
                    notEmptyAdapter.itemCount
                }
                val row = itemCount / maxColumn
                val totalVerticalSpacing = row * dp(gridSpacing)
                totalWidth = measureWidth
                totalHeight = if (itemCount % maxColumn != 0) {
                    itemHeight * (row + 1) + totalVerticalSpacing
                } else {
                    itemHeight * row + totalVerticalSpacing
                }

                gridWidth = itemWidth
                gridHeight = itemHeight
            }
        }

        for (i in 0 until childCount) {
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(gridWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(gridHeight, MeasureSpec.EXACTLY)
            )
        }

        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (adapter == null) {
            Log.w(TAG, "adapter is null, onLayout will skip.")
            return
        }
//        Log.d(TAG, "changed: ${changed} l : ${l} t: $t r: $r b : $b")
        val notEmptyAdapter = adapter!!

        val itemCount =
            if (notEmptyAdapter.itemCount >= maxImageCount) maxImageCount else notEmptyAdapter.itemCount
        for (i in 0 until itemCount) {
            val itemView = getChildAt(i)

            val row = i / maxColumn
            val col = i % maxColumn
            val left = paddingLeft + (gridWidth + dp(gridSpacing)) * col
            val right = left + gridWidth
            val top = paddingTop + (gridHeight + dp(gridSpacing)) * row
            val bottom = gridHeight + top
            itemView.layout(left, top, right, bottom)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val notEmptyAdapter = adapter!!

        val itemCount =
            if (notEmptyAdapter.itemCount >= maxImageCount) maxImageCount else notEmptyAdapter.itemCount
        post {
            for (i in 0 until itemCount) {
                notEmptyAdapter.bindViewHolder(viewHolders[i], i)
            }
        }
    }

    private fun dp(dp: Int): Int {
        return (resources.displayMetrics.density * dp).roundToInt()
    }

    fun setViewPool(viewPool: RecyclerView.RecycledViewPool) {
        this.viewPool = viewPool
    }

    fun setAdapter(adapter: Adapter) {
        val condition = conditions.first {
            it.weatherConditionApply(adapter.itemCount)
        }

        maxColumn = condition.maxColumn()
        maxImageCount = condition.maxShowCount()

        val itemCount = if (adapter.itemCount >= maxImageCount) maxImageCount else adapter.itemCount
        val notEmptyViewPool = this.viewPool!!

        if (this.adapter == null) {
            for (i in 0 until itemCount) {
                val itemType = adapter.getItemViewType(i)
                var viewHolder = notEmptyViewPool.getRecycledView(itemType)
                if (viewHolder == null) {
                    viewHolder = adapter.createViewHolder(this, itemType)
                    //bind view to viewHolder with view
                }
                viewHolders.add(viewHolder)
                //add view
                addView(viewHolder.itemView, generateDefaultLayoutParams())
            }
        } else {
            val preAdapterItemCount = viewHolders.size

            when {
                preAdapterItemCount > itemCount -> {
                    for (i in itemCount until preAdapterItemCount) {
                        val viewHolder = viewHolders[i]
                        //add viewHolder to pool
                        notEmptyViewPool.putRecycledView(viewHolder)
                    }
                    viewHolders.subList(itemCount, preAdapterItemCount).clear()

                    removeViews(adapter.itemCount, preAdapterItemCount - itemCount)
                }
                preAdapterItemCount < itemCount -> {
                    for (i in preAdapterItemCount until itemCount) {
                        val itemType = adapter.getItemViewType(i)
                        var viewHolder = notEmptyViewPool.getRecycledView(itemType)
                        if (viewHolder == null) {
                            viewHolder = adapter.createViewHolder(this, itemType)
                            //bind view to viewHolder with view
                        }
                        //add view
                        addView(viewHolder.itemView, generateDefaultLayoutParams())
                        viewHolders.add(viewHolder)
                    }
                }
            }

            //compare two adapter difference
            for (i in 0 until itemCount) {
                val preItemViewType = viewHolders[i].itemViewType
                val curItemViewType = adapter.getItemViewType(i)
                //item type is different
                if (preItemViewType != curItemViewType) {
                    //add viewHolder to pool
                    val holder = viewHolders.removeAt(i)
                    notEmptyViewPool.putRecycledView(holder)
                    //remove last different type view
                    removeViewAt(i)

                    var viewHolder = notEmptyViewPool.getRecycledView(curItemViewType)
                    if (viewHolder == null) {
                        viewHolder = adapter.createViewHolder(this, curItemViewType)
                    }

                    //add new type view
                    addView(viewHolder.itemView, i, generateDefaultLayoutParams())
                    viewHolders.add(i, viewHolder)
                }
            }
        }

        this.adapter = adapter
//        requestLayout()
    }

    fun addCondition(condition: Condition) {
        conditions.add(0, condition)
    }

    fun addConditionOfEnd(condition: Condition) {
        conditions.add(condition)
    }

    fun addAllCondition(conditions: List<Condition>) {
        this.conditions.addAll(conditions)
    }

    fun conditions(): List<Condition> {
        return conditions
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
}