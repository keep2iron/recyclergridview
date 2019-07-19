package io.github.keep2iron.recyclergridview

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.facebook.common.util.ByteConstants
import io.github.keep2iron.pineapple.ImageLoaderConfig
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.MiddlewareView

class MainActivity : AppCompatActivity() {

    val dataSource = arrayListOf(
            R.drawable.ic_gakii_pic1_01,
            R.drawable.ic_gakii_pic1_02,
            R.drawable.ic_gakii_pic1_03,
            R.drawable.ic_gakii_pic1_04,
            R.drawable.ic_gakii_pic1_05,
            R.drawable.ic_gakii_pic1_06,
            R.drawable.ic_gakii_pic1_07,
            R.drawable.ic_gakii_pic1_08,
            R.drawable.ic_gakii_pic1_09)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ImageLoaderManager.init(
                application,
                ImageLoaderConfig(
                        applicationContext,
                        maxCacheCount = 300,
                        maxCacheSize = (400 * ByteConstants.MB).toLong(),
                        cacheDirName = "cache_images",
                        cacheDirPath = cacheDir
                ),
                defaultImageLoaderOptions = {
                    scaleType = ImageLoaderOptions.ScaleType.FOCUS_CROP
                }
        )

        val items = arrayListOf(
                Data(generateImages(9)),
                Data(generateImages(8)),
                Data(generateImages(7)),
                Data(generateImages(6)),
                Data(generateImages(5)),
                Data(generateImages(4)),
                Data(generateImages(3)),
                Data(generateImages(2)),
                Data(generateImages(1)),
                Data(generateImages(9)),
                Data(generateImages(8)),
                Data(generateImages(7)),
                Data(generateImages(6)),
                Data(generateImages(5)),
                Data(generateImages(4)),
                Data(generateImages(3)),
                Data(generateImages(2)),
                Data(generateImages(1)),
                Data(generateImages(9))
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setRecycledViewPool(viewPool)
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val holder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
                val gridView = holder.itemView.findViewById<RecyclerGridView>(R.id.recyclerGridView)
                gridView.setViewPool(viewPool)
                gridView.addCondition(object : Condition() {

                    override fun maxColumn(): Int = 2

                    override fun weatherConditionApply(count: Int): Boolean = count == 3

                })
                gridView.addCondition(object : Condition() {

                    override fun maxPercentLayoutInParent(): Float = 0.75f

                    override fun aspectRatio(): Float {
                        return 0.835294118f
                    }

                    override fun maxColumn(): Int = 1

                    override fun weatherConditionApply(count: Int): Boolean = count == 1

                })
                return holder
            }

            override fun getItemCount(): Int = items.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val item = items[position]
                val gridView = holder.itemView.findViewById<RecyclerGridView>(R.id.recyclerGridView)
                ImageLoaderManager.getInstance().showImageView(holder.itemView.findViewById(R.id.ivAvatar), R.drawable.ic_avatar) {
                    isCircleImage = true
                }

                gridView.setAdapter(object : RecyclerGridView.Adapter() {
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
                        val itemViewType = getItemViewType(pos)
                        Log.d("MainActivity", "onBindViewHolder : ${position} , ${pos} ${pos == item.imageVies.size - 1} ${item.imageVies.size} ${itemViewType}")
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
                })
            }
        }
    }

    fun generateImages(size: Int): List<Int> {
        return dataSource.subList(0, size)
    }

    private fun dp(dp: Int): Float {
        return dp * resources.displayMetrics.density
    }


    data class Data(val imageVies: List<Int>)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
