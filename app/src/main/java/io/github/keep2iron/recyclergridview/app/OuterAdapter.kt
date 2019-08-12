package io.github.keep2iron.recyclergridview.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.recyclergridview.DefaultCondition
import io.github.keep2iron.recyclergridview.RecyclerGridView
import io.github.keep2iron.recyclergridview.SingleCondition
import io.github.keep2iron.recyclergridview.TwoXTwoCondition

class OuterAdapter(
  val list: List<Data>,
  val viewPool: androidx.recyclerview.widget.RecyclerView.RecycledViewPool
) : RecyclerView.Adapter<CustomViewHolder>() {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): CustomViewHolder {
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

  override fun onBindViewHolder(
    holder: CustomViewHolder,
    position: Int
  ) {
    val gridView = holder.itemView.findViewById<RecyclerGridView>(R.id.recyclerGridView)
    ImageLoaderManager.getInstance()
      .showImageView(holder.itemView.findViewById(R.id.ivAvatar), R.drawable.ic_avatar) {
        isCircleImage = true
      }

    val tag: Any? = holder.itemView.tag
    val adapter = if (tag == null) {
      val adapter = MyGridAdapter(holder.itemView.context, list[position])
      holder.itemView.tag = adapter
      adapter
    } else {
      (tag as MyGridAdapter).setItem(list[position])
      tag
    }
    gridView.setAdapter(adapter)
  }

  override fun getItemViewType(position: Int): Int {
    return list[position].imageVies.size
  }

  override fun getItemId(position: Int): Long {
    return list[position].hashCode().toLong()
  }
}