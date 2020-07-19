# rss-poller-recyclerview
A Kotlin abstraction for Android recyclerview adpater which executes periodic RSS polling.

## Example
 1. Declare a class that implements `rsspoller.recyclerview.RssPollerRecyclerViewAdapter`
 2. Please inject an activity in order to provide the UI manipulation thread.
```kotlin
class MyRssRecyclerViewAdapterAdapter(activity: Activity, options: RssPollerRecyclerViewOptions)
    : RssPollerRecyclerViewAdapter<MyRssRecyclerViewAdapterAdapter.ViewHolder>(activity, options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_rss_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, element: Element) {
        holder.textView.setText(
            element.text()
        )
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val textView: TextView

        init {
            textView = v.findViewById<TextView>(R.id.textView)
        }
    }
}
```

 3. After instantiating an `RssPollerRecyclerViewAdapter` object, inject this into a `RecyclerView` object.
 ```kotlin
 //somewhere in MainActivity.kt
 fun loadRssFeed() {
        RssFeed.getInstanceAsync("https://binchoo.tistory.com/rss") {
            runOnUiThread {
                setupRecyclerView(it)
            }
        }
    }

    fun setupRecyclerView(feed: RssFeed) {
        val ref = feed.getReference().child("item > title").sort(SortStrategy.TextLength(false))
        val options = RssPollerRecyclerViewOptions.Builder()
            .pollInterval(1500).reference(ref)
            .build()

        adapter = MyRssRecyclerViewAdapterAdapter(this, options)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
 ```
