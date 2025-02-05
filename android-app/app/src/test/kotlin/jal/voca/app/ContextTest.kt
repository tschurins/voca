package jal.voca.app

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ContextTest {
    @Test
    fun testSort() {
        val sort = SortOptions(numericPattern = "AB(\\d+)C(\\d+)")
        val col = TreeSet(sort.getComparator())
        col.add("AB1C14")
        col.add("AB1C5")
        col.add("AB1C123")
        col.add("AB1C2")

        val it = col.iterator()
        val first = it.next()
        val second = it.next()
        val third = it.next()
        val fourth = it.next()
        assertEquals("AB1C2", first)
        assertEquals("AB1C5", second)
        assertEquals("AB1C14", third)
        assertEquals("AB1C123", fourth)
    }
}