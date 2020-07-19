package rsspoller

import org.jsoup.nodes.Document
import org.jsoup.Connection
import org.jsoup.select.Elements
import rsspoller.sort.SortStrategy
import kotlin.collections.HashMap

class RssReference(private var connection: Connection,
                   private val cssQuery: String, val parent: RssReference?) {

    constructor(connection: Connection): this(connection, QUERY_DEFAULT, null)
    constructor(connection: Connection, cssQuery: String): this(connection, cssQuery, null)

    private lateinit var document: Document //only the first reference can possess a late-initialized document.
    var sortStrategy: SortStrategy<*>? = null
        private set

    private var elementsCache: HashMap<String, Elements> = HashMap()

    fun child(cssQuery: String): RssReference {
        val childCssQuery = concatQuery(cssQuery)
        return RssReference(connection, childCssQuery, this)
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
     * @return Elements newly evaluated if forceEval=true or isEvalutated()=false, else read from cache.
     * @author binchoo
     */
    fun elems(forceEval: Boolean = true): Elements {
        evaluate(forceEval, this)
        return cachedElementsNonNull()
    }

    @Synchronized
    fun evaluate(forceEval: Boolean, initiator: RssReference) {
        if (!isEvaluated() || forceEval) {
            val myDocument = if (parent == null) {
                lazyConnection()
            } else {
                parent.evaluate(forceEval, initiator)
                parent.asDocument()
            }

            if (this == initiator || parent == null || hasSortStrategy())
                parseMyDocument(myDocument)
        }
    }

    fun lazyConnection(): Document {
        document = connection.get()
        return document
    }

    private fun asDocument(): Document {
        return if (isFirstReference())
            document
        else Document("").also {document->
            document.html(cachedElementsNonNull().html())
        }
    }

    private fun parseMyDocument(document: Document) {
        val elems = if (isQuerySpecified())
                document.select(cssQuery)
            else
                document.allElements
        sortStrategy?.sort(elems)
        elementsCache.put(cssQuery, elems)
    }

    fun cachedElements() =
        elementsCache[cssQuery]

    private fun cachedElementsNonNull() =
        cachedElements()!!

    fun isEvaluated(): Boolean =
        elementsCache[cssQuery] != null

    fun isQuerySpecified(): Boolean =
        !cssQuery.equals(QUERY_DEFAULT)

    fun isFirstReference(): Boolean =
        parent == null

    fun hasSortStrategy(): Boolean =
        sortStrategy != null

    companion object {
        private val QUERY_DEFAULT = ""
    }
}