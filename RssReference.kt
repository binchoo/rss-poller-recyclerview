package rsspoller

import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import rsspoller.sort.SortStrategy
import java.util.*
import kotlin.collections.HashMap

class RssReference(private var document: Document,
                   private val cssQuery: String, val parent: RssReference?) {

    constructor(document: Document): this(document, QUERY_DEFAULT, null)
    constructor(document: Document, cssQuery: String): this(document, cssQuery, null)

    private var evalQueue: LinkedList<RssReference> =
        if (parent == null) {
            LinkedList()
        } else {
            parent.evalQueue.clone() as LinkedList<RssReference>
        }.also {evalQueue->
            evalQueue.add(this)
        }

    var sortStrategy: SortStrategy<*>? = null
        private set

    private var elementsCache: HashMap<String, Elements> = HashMap()

    fun child(cssQuery: String): RssReference {
        val childCssQuery = concatQuery(cssQuery)
        return RssReference(document, childCssQuery, this)
    }

    private fun concatQuery(cssQuery: String): String {
        return if (hasSortStrategy() || !isQuerySpecified()) cssQuery
        else "${this.cssQuery} > $cssQuery"
    }

    fun sort(sortStrategy: SortStrategy<*>): RssReference {
        this.sortStrategy = sortStrategy
        return this
    }

    fun noSort(): RssReference {
        this.sortStrategy = null
        return this
    }

    /**
     * @param forceEval whether or not to force the lazy evaluation. Default true.
     * @return Elements, read from cache if forceEval=false, else newly evaluated.
     * @author binchoo
     */
    @Synchronized
    fun elems(forceEval: Boolean = true): Elements {
        return evaluateQueue(evalQueue, forceEval)
    }

    fun isQuerySpecified(): Boolean =
        !cssQuery.equals(QUERY_DEFAULT)

    fun isEvaluated(): Boolean =
        elementsCache[cssQuery] != null

    fun hasSortStrategy(): Boolean =
        sortStrategy != null

    private fun writeToCache() {
        val elems =
            if (isQuerySpecified())
                document.select(cssQuery)
            else
                document.allElements
        sortStrategy?.sort(elems)
        elementsCache.put(cssQuery, elems)
    }

    fun readFromCache() =
        elementsCache[cssQuery]

    private fun readFromCacheNotNull() =
        readFromCache()!!

    companion object {
        private val QUERY_DEFAULT = ""

        private fun evaluateQueue(referenceEvalQueue: Queue<RssReference>, forceEval: Boolean): Elements {
            val last = referenceEvalQueue.last()
            var subDocument = referenceEvalQueue.first().document

            referenceEvalQueue.forEach { ref ->
                if (ref == last || ref.hasSortStrategy()) {
                    ref.document = subDocument
                    subDocument = elems2doc(evaluateReference(ref, forceEval))
                }
            }
            return last.readFromCacheNotNull()
        }

        private fun evaluateReference(ref: RssReference, forceEval: Boolean): Elements {
            if (!ref.isEvaluated() || forceEval)
                ref.writeToCache()
            return ref.readFromCacheNotNull()
        }

        private fun elems2doc(elems: Elements): Document {
            return Document("").also {
                it.html(elems.html())
            }
        }
    }
}