package jal.voca.lang

import org.junit.jupiter.api.Assertions.*
import kotlin.test.*

class WordPartsTest {
    @Test
    fun multipleParts() {
        val parts = WordParts("abc/ def/ghi / jkl")
        assertEquals(4, parts.all.size)
        assertEquals("", parts.comment)
        assertEquals("abc / def / ghi / jkl", parts.toString())
    }

    @Test
    fun comment() {
        val parts = WordParts("abc(comment)")
        assertEquals(" (comment)", parts.comment)
        assertEquals("abc (comment)", parts.toString())
    }

    @Test
    fun map() {
        val parts = WordParts("abc / def (comment)")
        val converted = parts.map { "a-" + it }
        assertEquals("a-abc / a-def (comment)", converted.toString())
    }
}