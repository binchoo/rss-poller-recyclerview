package rsspoller

import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import rsspoller.sort.SortStrategy
import kotlin.collections.HashMap

abstract class RssReference(private val cssQuery: String, val parent: RssReference?=null) {
    lateinit var document: Document
    private var elementsCache: HashMap<String, Elements> = HashMap()
    var sortStrategy: SortStrategy<*>? = null
        private set

    /**
     * @param forceEval whether or not to force the lazy evaluation. Default true.
     * @return Elements newly evaluated if forceEval=true or isEvalutated()=false, else read from cache.
     * @author binchoo
     */
    fun elems(forceEval: Boolean = true): Elements {
        if (!isEvaluated() || forceEval)
            evaluate(this)
        return cachedElementsNonNull()
    }

    @Synchronized
    fun evaluate(initiator: RssReference)  {
        parent?.evaluate(initiator)
        document = getTargetDocument()
        if (this == initiator || hasSortStrategy())
            documentSelection(document)
    }

    private fun documentSelection(document: Document) {
        val elems =
            if (isQuerySpecified())
                document.select(cssQuery)
            else
                document.allElements
        sortStrategy?.sort(elems)
        elementsCache.put(cssQuery, elems)
    }

    protected abstract fun getTargetDocument(): Document

    fun sort(sortStrategy: SortStrategy<*>): RssReference {
        this.sortStrategy = sortStrategy
        return this
    }

    fun noSort(): RssReference {
        this.sortStrategy = null
        return this
    }

    abstract fun child(cssQuery: String): RssReference

    protected fun queryForChild(cssQuery: String): String {
        return if (hasSortStrategy() || !isQuerySpecified()) cssQuery
        else "${this.cssQuery} > $cssQuery"
    }

    fun cachedElements() =
        elementsCache[cssQuery]

    protected fun cachedElementsNonNull() =
        cachedElements()!!

    fun isEvaluated(): Boolean =
        elementsCache[cssQuery] != null

    fun isQuerySpecified(): Boolean =
        !cssQuery.equals(QUERY_EMPTY)

    fun hasSortStrategy(): Boolean =
        sortStrategy != null

    companion object {
        val QUERY_EMPTY = ""
    }
}