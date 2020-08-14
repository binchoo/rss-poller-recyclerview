# rss-poller-recyclerview
RssPollerRecyclerViewAdapter is a Kotlin abstraction of Android RecyclerView.Adapter, which executes periodic RSS polling.
Make your recyclerview responsive to the RSS feed.

## Example
 1. Declare a class that implements `rsspoller.recyclerview.RssPollerRecyclerViewAdapter`
 2. Please inject an activity, which will provide the UI thread.
```kotlin
class MyRssRecyclerViewAdapter(activity: Activity, options: RssPollerRecyclerViewOptions)
    : RssPollerRecyclerViewAdapter<MyRssRecyclerViewAdapter.ViewHolder>(activity, options) {

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
 3. Create a `RssFeed` object. `RssFeed` abstracts a RSS document (any XML/HTML document) on the web.
 4. Point the place where you want to get by calling `RssFeed.getReference(cssQuery: String)`, `RssReference.child(cssQuery: String)`.
 5. You can sort the elements reference by reference. Use `RssReference.sort()` where a `SortStrategy` must be injected.
 6. To have a `RecyclerView` reflect the update the RssReference receives, build a `RssPollerRecyclerViewOptions`.
 6. Inject it into your `RssPollerRecyclerViewAdapter`. Now then, make the `RecyclerView` use your adapter.
 ```kotlin
//somewhere in MainActivity.kt
    fun loadRssFeed() {
        feed = RssFeed.getInstance("https://binchoo.tistory.com/rss")
    }

    fun setupRecyclerView(feed: RssFeed) {
        val ref = feed.getReference().child("item > title").sort(SortStrategy.TextLength(false))
        val options = RssPollerRecyclerViewOptions.Builder()
            .pollInterval(5000).reference(ref)
            .build()

        adapter = MyRssRecyclerViewAdapter(this, options)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
 ```
## Fix Issues
 - ~~Add lazy evaluation feature for RssReference~~
 - ~~Fix race condition when executing lazy evaluation~~
 - ~~Add lazy evaluation feature for Document~~
 - ~~Fix malfunction of connection toward RSS formatted document. (on JVM Kotlin environment only)~~
