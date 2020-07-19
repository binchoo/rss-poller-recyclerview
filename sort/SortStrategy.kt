package rsspoller.sort

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

open class SortStrategy<CriterionType: Comparable<CriterionType>>(private var sortCriterion: SortCriterion<CriterionType>, val isAscending: Boolean) {
    fun sort(elements: Elements) {
        when (isAscending) {
            true -> elements.sortBy { element ->
                __criterionValueOf(element)
            }
            false -> elements.sortByDescending { element ->
                __criterionValueOf(element)
            }
        }
    }

    private fun __criterionValueOf(element: Element): CriterionType {
        return sortCriterion.criterionValueOf(element)
    }

    class AttrValue(private val attrKey: String, isAscending: Boolean = true)
        : SortStrategy<String>(object: SortCriterion<String> {
            override fun criterionValueOf(element: Element): String {
                return element.attr(attrKey)
            }
        }, isAscending)

    class ChildrenCount(isAscending: Boolean = true)
        : SortStrategy<Int>(object: SortCriterion<Int> {
            override fun criterionValueOf(element: Element): Int {
                return element.children().size
            }
        }, isAscending)

    class ClassName(isAscending: Boolean = true)
        : SortStrategy<String>(object: SortCriterion<String> {
            override fun criterionValueOf(element: Element): String {
                return element.className()
            }
        }, isAscending)

    class TagName(isAscending: Boolean = true)
        : SortStrategy<String>(object: SortCriterion<String> {
            override fun criterionValueOf(element: Element): String {
                return element.tagName()
            }
        }, isAscending)

    class Text(isAscending: Boolean = true)
        : SortStrategy<String>(object: SortCriterion<String> {
            override fun criterionValueOf(element: Element): String {
                return element.text()
            }
        }, isAscending)

    class TextLength(isAscending: Boolean = true)
        : SortStrategy<Int>(object: SortCriterion<Int> {
            override fun criterionValueOf(element: Element): Int {
                return element.text().length
            }
        }, isAscending)

    class HtmlLength(isAscending: Boolean = true)
        : SortStrategy<Int>(object: SortCriterion<Int> {
            override fun criterionValueOf(element: Element): Int {
                return element.html().length
            }
        }, isAscending)
}
