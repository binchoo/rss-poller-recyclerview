package rsspoller

import org.jsoup.Connection
import org.jsoup.nodes.Document

class RootReference(private val connection: Connection, cssQuery: String=QUERY_EMPTY)
    : RssReference(cssQuery) {

    override fun child(cssQuery: String): RssReference {
        val childCssQuery = queryForChild(cssQuery)
        return ChildReference(childCssQuery, this)
    }

    override fun getTargetDocument(): Document {
        return connection.get()
    }
}