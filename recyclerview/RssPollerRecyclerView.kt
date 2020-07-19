package rssfeeder.recyclerview

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.corndog.rssfeederrecyclerview.rssfeeder.RssPoller
import org.jsoup.nodes.Element
import rsspoller.RssReference

abstract class RssPollerRecyclerView<VH: RecyclerView.ViewHolder>(activity: Activity, val options: RssPollerRecyclerViewOptions)
    : RecyclerView.Adapter<VH>() {

    private val rssReference = options.reference
    private val rssPoller = RssPoller(rssReference, options.pollInterval).also {rssPoller->
        rssPoller.addCallback(object: RssPoller.Callback {
            override fun onEachPolling(updatedRssReference: RssReference) {
                activity.runOnUiThread {
                    notifyDataSetChanged()
                }
            }
        })
    }

    fun startPolling() {
        rssPoller.start()
    }

    fun stopPolling() {
        rssPoller.stop()
    }

    fun notifyOptionsUpdated() {
        rssPoller.changeInterval(options.pollInterval)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, position, readElement(position))
    }

    protected abstract fun onBindViewHolder(holder: VH, position: Int, element: Element)

    private fun readElement(position: Int): Element {
        return rssReference.readFromCache()!!.get(position)
    }

    override fun getItemCount(): Int {
        return rssReference.readFromCache()?.size ?: 0
    }
}