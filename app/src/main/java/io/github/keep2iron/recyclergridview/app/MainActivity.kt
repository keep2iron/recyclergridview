package io.github.keep2iron.recyclergridview.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.facebook.common.util.ByteConstants
import io.github.keep2iron.pineapple.ImageLoaderConfig
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions

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
        R.drawable.ic_gakii_pic1_09
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
        recyclerView.adapter = OuterAdapter(items, viewPool)
    }

    fun generateImages(size: Int): List<Int> {
        return dataSource.subList(0, size)
    }

    private fun dp(dp: Int): Float {
        return dp * resources.displayMetrics.density
    }


}
