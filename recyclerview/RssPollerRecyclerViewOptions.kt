package rsspoller.recyclerview

import rsspoller.RssReference
import java.lang.NullPointerException

class RssPollerRecyclerViewOptions private constructor(val reference: RssReference) {
    var pollInterval: Long = 5000

    class Builder {
        private var rssReference: RssReference? = null
        private var pollInterval: Long = 5000

        fun reference(reference: RssReference): Builder {
            rssReference = reference
            return this
        }

        fun pollInterval(ms: Long): Builder {
            pollInterval = ms
            return this
        }

        fun build(): RssPollerRecyclerViewOptions {
            if (rssReference == null) throw NullPointerException()
            return RssPollerRecyclerViewOptions(rssReference!!).also {
                it.pollInterval = pollInterval
            }
        }
    }
}