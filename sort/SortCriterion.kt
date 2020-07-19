package rsspoller.sort

import org.jsoup.nodes.Element

interface SortCriterion <CriterionType: Comparable<CriterionType>>  {
    fun criterionValueOf(element: Element): CriterionType
}