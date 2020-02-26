package io.github.keep2iron.recyclergridview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.recyclergridview.util.AspectRatio
import java.lang.IllegalArgumentException
import java.util.LinkedList
import kotlin.math.max
import kotlin.math.roundToInt

class RecyclerGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "RecyclerGridView"
    }

    //grid space
    private var gridSpacing = 5

//    private var gridWidth = 0
//
//    private var gridHeight = 0

    private var conditions = LinkedList<Condition>()

    //set from condition
    private var maxColumn = -1
    //set from condition
    private var maxImageCount = -1

    private var adapter: Adapter? = null

    //用于盘点bindAdapter的标志,因为recyclerView中可能连续存在相同大小的Item因此设置这个标志
    private var resetFlag = false

    private var viewHolders = mutableListOf<RecyclerView.ViewHolder>()

    private var recyclerViewPool: RecyclerView.RecycledViewPool? = null

    open class InternalViewHolder(itemView: View) :
        RecyclerView.ViewHolder(
            itemView
        )

    abstract class Adapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(
            viewParent: ViewGroup,
            viewType: Int
        ): InternalViewHolder {
            return InternalViewHolder(onCreateView(viewParent, viewType))
        }

        abstract fun onCreateView(
            viewParent: ViewGroup,
            viewType: Int
        ): View

        override fun getItemViewType(position: Int): Int = 1024
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (adapter == null) {
            Log.w(TAG, "adapter is null, onMeasure will skip.")
            return
        }

        val notEmptyAdapter = adapter!!

        val condition = conditions.first {
            it.weatherConditionApply(notEmptyAdapter.itemCount)
        }

        val totalWidth: Int = (condition.maxPercentLayoutInParent * MeasureSpec.getSize(widthMeasureSpec)).roundToInt()
        var totalHeight: Int = 0

        val itemCount = if (notEmptyAdapter.itemCount >= maxImageCount) {
            maxImageCount
        } else {
            notEmptyAdapter.itemCount
        }
        val itemWidth =
            ((totalWidth - (maxColumn - 1) * dp(gridSpacing)) / (maxColumn * 1f)).roundToInt()
        val row = itemCount / maxColumn
        val totalVerticalSpacing = row * dp(gridSpacing)

        when (condition.itemAspectRatio.mode) {
            AspectRatio.MODE_EXACTLY, AspectRatio.MODE_RATIO -> {

                val itemHeight = condition.itemAspectRatio.calculateMeasureSpec(itemWidth)
                totalHeight = if (itemCount == 1) {
                    itemHeight
                } else if(itemCount != 1 && itemCount % maxColumn != 0){
                    itemHeight * (row + 1) + totalVerticalSpacing
                } else {
                    itemHeight * row + totalVerticalSpacing
                }

                for (i in 0 until childCount) {
                    getChildAt(i).measure(
                        MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY)
                    )
                }
            }
            AspectRatio.MODE_WRAP -> {
                val row = itemCount / maxColumn
                val totalVerticalSpacing = row * dp(gridSpacing)

                //只在wrap模式下该值有效
                var maxWrapRowSize = 0

                for (i in 0 until itemCount) {
                    val columnIndex = i % maxColumn

                    getChildAt(i).measure(
                        MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    maxWrapRowSize = max(maxWrapRowSize,getChildAt(i).measuredHeight)
                    if(columnIndex == maxColumn - 1 || (i  + 1== itemCount)){
                        totalHeight += (maxWrapRowSize + totalVerticalSpacing)
                    }else if(columnIndex == 0){
                        maxWrapRowSize = getChildAt(i).measuredHeight
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("mode is illegal!")
            }
        }

        setMeasuredDimension(totalWidth, totalHeight)
    }

    fun findRecyclerViewPool(view: View?) {
        if (view == null) {
            throw IllegalArgumentException("you should nested in a RecyclerView")
        }

        when {
            view is RecyclerView -> {
                this.recyclerViewPool = (view as RecyclerView).recycledViewPool
            }
            view.parent is View -> {
                findRecyclerViewPool(view.parent as View)
            }
            else -> {
                findRecyclerViewPool(null)
            }
        }
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        if (adapter == null) {
            Log.w(TAG, "adapter is null, onLayout will skip.")
            return
        }
        val notEmptyAdapter = adapter ?: throw IllegalArgumentException("adapter is null.")

        val itemCount =
            if (notEmptyAdapter.itemCount >= maxImageCount) maxImageCount else notEmptyAdapter.itemCount

        val condition = conditions.first {
            it.weatherConditionApply(notEmptyAdapter.itemCount)
        }

        //只在wrap模式下该值有效
        var wrapRowTop = 0
        var maxWrapRowSize = 0
        var lastRow = 0

        for (i in 0 until itemCount) {
            val itemView = getChildAt(i)
            val itemWidth =
                ((measuredWidth - (maxColumn - 1) * dp(gridSpacing)) / (maxColumn * 1f)).roundToInt()

            val row = i / maxColumn
            val col = i % maxColumn
            val left = paddingLeft + (itemWidth + dp(gridSpacing)) * col
            val right = left + itemWidth

            when (condition.itemAspectRatio.mode) {
                AspectRatio.MODE_EXACTLY, AspectRatio.MODE_RATIO -> {
                    val itemHeight = condition.itemAspectRatio.calculateMeasureSpec(itemWidth)
                    val top = paddingTop + (itemHeight + dp(gridSpacing)) * row
                    val bottom = itemHeight + top
                    itemView.layout(left, top, right, bottom)
                }
                AspectRatio.MODE_WRAP -> {
                  if(lastRow != row){
                    lastRow = row
                    wrapRowTop += (maxWrapRowSize + dp(gridSpacing))
                  }else{
                    maxWrapRowSize = max(maxWrapRowSize,getChildAt(i).measuredHeight)
                  }

                  val top = paddingTop + wrapRowTop
                  val bottom = getChildAt(i).measuredHeight + top
                  itemView.layout(left, top, right, bottom)
                }
                else -> {
                    throw IllegalArgumentException("mode is illegal!")
                }
            }

            if (resetFlag) {
                notEmptyAdapter.bindViewHolder(viewHolders[i], i)
            }
        }
        resetFlag = false
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        val notEmptyAdapter = adapter!!

        if (resetFlag) {
            val itemCount =
                if (notEmptyAdapter.itemCount >= maxImageCount) maxImageCount else notEmptyAdapter.itemCount
            post {
                for (i in 0 until itemCount) {
                    notEmptyAdapter.bindViewHolder(viewHolders[i], i)
                }
                resetFlag = false
            }
        }
    }

    private fun dp(dp: Int): Int {
        return (resources.displayMetrics.density * dp).roundToInt()
    }

    fun setAdapter(adapter: Adapter) {
        this.adapter = adapter

        checkNotNull(recyclerViewPool) { "you must call findRecyclerViewPool() first!" }
        val recyclerViewPool = this.recyclerViewPool!!

        val condition = conditions.first {
            it.weatherConditionApply(adapter.itemCount)
        }

        maxColumn = condition.maxColumn
        maxImageCount = condition.maxShowCount

        val itemCount = if (adapter.itemCount >= maxImageCount) maxImageCount else adapter.itemCount

        if (viewHolders.isEmpty()) {
            for (i in 0 until itemCount) {
                val itemType = adapter.getItemViewType(i)
                var viewHolder = recyclerViewPool.getRecycledView(itemType)
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
                        recyclerViewPool.putRecycledView(viewHolder)
                    }
                    viewHolders.subList(itemCount, preAdapterItemCount)
                        .clear()

                    removeViews(adapter.itemCount, preAdapterItemCount - itemCount)
                }
                preAdapterItemCount < itemCount -> {
                    for (i in preAdapterItemCount until itemCount) {
                        val itemType = adapter.getItemViewType(i)
                        var viewHolder = recyclerViewPool.getRecycledView(itemType)
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
                    recyclerViewPool.putRecycledView(holder)
                    //remove last different type view
                    removeViewAt(i)

                    var viewHolder = recyclerViewPool.getRecycledView(curItemViewType)
                    if (viewHolder == null) {
                        viewHolder = adapter.createViewHolder(this, curItemViewType)
                    }

                    //add new type view
                    addView(viewHolder.itemView, i, generateDefaultLayoutParams())
                    viewHolders.add(i, viewHolder)
                }
            }
        }

        resetFlag = true
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