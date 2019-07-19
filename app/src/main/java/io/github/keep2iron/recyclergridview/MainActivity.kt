package io.github.keep2iron.recyclergridview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.facebook.common.util.ByteConstants
import io.github.keep2iron.pineapple.ImageLoaderConfig
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.MiddlewareView

class MainActivity : AppCompatActivity() {

    val picSource = arrayOf(
        "https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/whfpf%3D280%2C150%2C0/sign=6f533a1a9d13b07ebde803486aeaa31b/562c11dfa9ec8a13f786eadbf903918fa0ecc069.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR2T27DhRloKyklUtYIRGgbIzL-2C_KsQmfbMQWvxVtDkpGszJz",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQAUKfWalG7KuonN6lebE-5cyR65upoFwCVF8EP-9g-MdmKu2YO",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQjmaB4hlZ8Yc20wFTf76eJDq3YrNAUKCVlD_0usFkfzMu1MD5ueA",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8j_Ue_YRaiiAk738YguGMjTJEGGknIFSJEf9hBuBpJLBVJZE2lQ",
        "http://www.jd-tv.com/uploads/allimg/180127/18-1P12FZ028.jpg",
        "http://himg2.huanqiu.com/attachment2010/2018/0601/20180601041830165.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7SjhFegGlYIia50LdGDKzuVXJ6144KGsPdqi50YB-1aEAgPJAfA",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQAZExevVv4JlzCIOwjSTL5CqYKcgVunM41LlY-l69p6bsEsNZsPg"
    )

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
            Data(generateImages(1))
            , Data(generateImages(2))
            , Data(generateImages(3))
            , Data(generateImages(4))
            , Data(generateImages(5))
            , Data(generateImages(6))
            , Data(generateImages(7))
            , Data(generateImages(8))
            , Data(generateImages(9))
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
                        return 0.5f
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
                gridView.setAdapter(object : RecyclerGridView.Adapter() {
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
                        Log.d("MainActivity", "onBindViewHolder : ${position} , ${pos}")
                        ImageLoaderManager.getInstance().showImageView(
                            holder.itemView as MiddlewareView,
                            item.imageVies[pos]
                        ) {

                        }
                    }

                    override fun getItemCount(): Int = item.imageVies.size

                    override fun onCreateView(viewParent: ViewGroup, viewType: Int): View {
                        return MiddlewareView(viewParent.context)
                    }
                })
            }
        }
    }

    fun generateImages(size: Int): ArrayList<String> {
        val list = arrayListOf<String>()
        if (size == 1) return arrayListOf("https://www.chuangkit.com/yy-folder/img/ctp4.jpg")
        for (i in 0 until size) {
            list.add(picSource[(Math.random() * picSource.size).toInt()])
        }
        return list
    }


    data class Data(val imageVies: List<String>)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
