package io.github.keep2iron.recyclergridview.app

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.MiddlewareView
import io.github.keep2iron.recyclergridview.RecyclerGridView

class MyGridAdapter(val context: Context,private val item: Data) : RecyclerGridView.Adapter() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val itemViewType = getItemViewType(pos)
        if (itemViewType == 10003) {
            ImageLoaderManager.getInstance().showImageView(
                holder.itemView.findViewById(R.id.imageView) as MiddlewareView,
                item.imageVies[pos]
            ) {
                radiusTopLeft = if (pos == 0) dp(10) else 0f
                radiusTopRight = if (pos == 2) dp(10) else 0f
                radiusBottomRight = if (pos == 6) dp(10) else 0f
                radiusBottomLeft = if (pos == 8) dp(10) else 0f
            }
            holder.itemView.findViewById<TextView>(R.id.tvCount).text = "+" + item.imageVies.size
        } else {
            ImageLoaderManager.getInstance().showImageView(
                holder.itemView as MiddlewareView,
                if (item.imageVies.size > 1) item.imageVies[pos] else R.drawable.ic_origin
            ) {
                if (item.imageVies.size > 1) {
                    radiusTopLeft = if (pos == 0) dp(10) else 0f
                    radiusTopRight = if (pos == 2) dp(10) else 0f
                    radiusBottomRight = if (pos == 6) dp(10) else 0f
                    radiusBottomLeft = if (pos == 8) dp(10) else 0f
                } else {
                }
            }
        }
    }

    override fun getItemCount(): Int = item.imageVies.size

    override fun onCreateView(viewParent: ViewGroup, viewType: Int): View {
        if (viewType == 10003) {
            return LayoutInflater.from(viewParent.context).inflate(R.layout.item_grid, null, false)
        }
        return MiddlewareView(viewParent.context)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == item.imageVies.size - 1 && item.imageVies.size != 1) {
            10003
        } else {
            super.getItemViewType(position)
        }
    }

    private fun dp(dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }
}