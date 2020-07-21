package rsspoller

import org.jsoup.nodes.Document

class ChildReference(cssQuery: String, parent: RssReference)
    : RssReference(cssQuery, parent) {

    override fun child(cssQuery: String): RssReference  {
        val childCssQuery = queryForChild(cssQuery)
        return ChildReference(childCssQuery, this)
    }

    override fun getTargetDocument(): Document {
        return parent!!.document
    }
}