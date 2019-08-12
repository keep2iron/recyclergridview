package io.github.keep2iron.recyclergridview.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
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
    LogMonitor.getInstance().startMonitor()
    BlockDetectByLooper.start()

    val items = arrayListOf(
      Data(generateImages(9)),
      Data(generateImages(8)),
      Data(generateImages(7)),
      Data(generateImages(6)),
      Data(generateImages(5)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(4)),
      Data(generateImages(0)),
      Data(generateImages(0)),
      Data(generateImages(3)),
      Data(generateImages(2)),
      Data(generateImages(1)),
      Data(generateImages(0)),
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
    val viewPool = RecycledViewPool()
    recyclerView.layoutManager =
      androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
    recyclerView.setRecycledViewPool(viewPool)
    val outerAdapter = OuterAdapter(items, viewPool)
    outerAdapter.setHasStableIds(true)
    recyclerView.adapter = outerAdapter
    recyclerView.setItemViewCacheSize(20);
    recyclerView.setDrawingCacheEnabled(true);
    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    ImageLoaderManager.getInstance().pause(recyclerView.context)

    recyclerView.addOnScrollListener(object : OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        ImageLoaderManager.getInstance().pause(recyclerView.context)
      }

      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          ImageLoaderManager.getInstance().resume(recyclerView.context)
        }
      }
    })
  }

  fun generateImages(size: Int): List<Int> {
    return dataSource.subList(0, size)
  }

  private fun dp(dp: Int): Float {
    return dp * resources.displayMetrics.density
  }

}
