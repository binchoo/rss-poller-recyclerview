package rsspoller

import org.jsoup.Connection
import org.jsoup.Jsoup

class RssFeed private constructor(private val connection: Connection) {
    private lateinit var rssReference: RssReference

    fun getReference(): RssReference {
        rssReference = RssReference(connection)
        return rssReference
    }

    fun getReference(cssQuery: String): RssReference {
        rssReference = RssReference(connection, cssQuery)
        return rssReference
    }

    companion object {
        fun getInstance(urlstr: String): RssFeed {
            return getInstance(Jsoup.connect(urlstr))
        }

        fun getInstance(jsoupConnection: Connection): RssFeed {
            val instance = RssFeed(jsoupConnection)
            return instance
        }
    }
}