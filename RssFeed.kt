package rsspoller

import org.jsoup.Connection
import org.jsoup.Jsoup

class RssFeed private constructor(private val connection: Connection) {
    private lateinit var rssReference: RssReference

    fun getReference(): RssReference {
        rssReference = RootReference(connection)
        return rssReference
    }

    fun getReference(cssQuery: String): RssReference {
        rssReference = RootReference(connection, cssQuery)
        return rssReference
    }

    companion object {
        private val agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/33.0.1750.152 Safari/537.36"

        fun getInstance(urlstr: String): RssFeed {
            return getInstance(Jsoup.connect(urlstr).userAgent(agent))
        }

        fun getInstance(jsoupConnection: Connection): RssFeed {
            val instance = RssFeed(jsoupConnection)
            return instance
        }
    }
}