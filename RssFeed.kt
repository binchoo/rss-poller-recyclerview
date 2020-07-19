package rsspoller

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class RssFeed private constructor(val document: Document) {
    private lateinit var rssReference: RssReference

    fun getReference(): RssReference {
        rssReference = RssReference(document)
        return rssReference
    }

    fun getReference(cssQuery: String): RssReference {
        rssReference = RssReference(document, cssQuery)
        return rssReference
    }

    //TODO: fatal: lazify document connection!
    companion object {
        fun getInstanceAsync(urlstr: String, callback: ((RssFeed)->Unit)) {
            return getInstanceAsync(Jsoup.connect(urlstr), callback)
        }

        fun getInstanceAsync(jsoupConnection: Connection, callback: ((RssFeed)->Unit)) {
            val connectionThread = Thread {
                val instance = getInstance(jsoupConnection)
                callback(instance)
            }
            connectionThread.start()
        }

        fun getInstance(urlstr: String): RssFeed {
            return getInstance(Jsoup.connect(urlstr))
        }

        fun getInstance(jsoupConnection: Connection): RssFeed {
            val instance = RssFeed(jsoupConnection.get())
            return instance
        }
    }
}