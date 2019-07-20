package io.github.keep2iron.recyclergridview.app

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.recyclergridview.DefaultCondition
import io.github.keep2iron.recyclergridview.RecyclerGridView
import io.github.keep2iron.recyclergridview.SingleCondition
import io.github.keep2iron.recyclergridview.TwoXTwoCondition

class OuterAdapter(
    val list: List<Data>,
    val viewPool: RecyclerView.RecycledViewPool
) :
    RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val holder = CustomViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view,
                parent,
                false
            )
        )
        val gridView = holder.itemView.findViewById<RecyclerGridView>(R.id.recyclerGridView)
        gridView.setViewPool(viewPool)
        gridView.addAllCondition(arrayListOf(SingleCondition {
            aspectRatio = 0.875f
            maxPercentLayoutInParent = 0.85f
        }, TwoXTwoCondition {
            maxPercentLayoutInParent = 0.75f
        }, DefaultCondition()))
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val gridView = holder.itemView.findViewById<RecyclerGridView>(R.id.recyclerGridView)
        ImageLoaderManager.getInstance()
            .showImageView(holder.itemView.findViewById(R.id.ivAvatar), R.drawable.ic_avatar) {
                isCircleImage = true
            }

        gridView.setAdapter(MyGridAdapter(holder.itemView.context, list[position]))
    }
}