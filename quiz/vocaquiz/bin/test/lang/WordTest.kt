package jal.voca.lang

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class WordTest {
    @Test
    fun testParsing_adjective() {
        val ti = TypeInfo.parseTypeInfo("a")
        assertNotNull(ti)
        assertEquals(ti!!.type, WordType.ADJECTIVE)
        assertEquals("a", ti!!.toString())
    }

    @Test
    fun testParsing_nounMasculine() {
        val ti = TypeInfo.parseTypeInfo("n(m)")
        assertNotNull(ti)
        assertEquals(ti!!.type, WordType.NOUN)
        assertEquals(ti!!.gender, Gender.MASCULINE)
        assertEquals(ti!!.cardinality, null)
        assertEquals(ti!!.pluralForm, null)
        assertEquals("n(m,,)", ti!!.toString())
    }

    @Test
    fun testParsing_nounFemininePlural() {
        val ti = TypeInfo.parseTypeInfo("n(f, pl)")
        assertNotNull(ti)
        assertEquals(ti!!.type, WordType.NOUN)
        assertEquals(ti!!.gender, Gender.FEMININE)
        assertEquals(ti!!.cardinality, Cardinality.PLURAL)
        assertEquals(ti!!.pluralForm, null)
        assertEquals("n(f,pl,)", ti!!.toString())
    }

    @Test
    fun testParsing_nounNeuterPluralForm() {
        val ti = TypeInfo.parseTypeInfo("n(n, , -s)")
        assertNotNull(ti)
        assertEquals(ti!!.type, WordType.NOUN)
        assertEquals(ti!!.gender, Gender.NEUTER)
        assertEquals(ti!!.cardinality, null)
        assertEquals(ti!!.pluralForm, "s")
        assertEquals("n(n,,s)", ti!!.toString())
    }
}