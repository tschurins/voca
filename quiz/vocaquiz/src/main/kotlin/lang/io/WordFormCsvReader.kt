package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

class WordFormCsvReader {
    fun readWordForms(formStream: InputStream) : List<WordCasesPerGender> {
        val br = BufferedReader(formStream.reader())
        val result: MutableList<WordCasesPerGender> = mutableListOf()
        try {
            var line: String? = br.readLine()
            val headers = line!!.split("|")
            val forms = getForms(headers)
            line = br.readLine()
            while (line != null) {
                val lineResult: MutableMap<WordForm, String> = mutableMapOf()
                val values = line.split("|")
                val gender = valueOf<Gender>(values[0])
                for (i in 1..<values.size) {
                    val form = forms[i - 1]
                    lineResult[form] = values[i]
                }
                result.add(WordCasesPerGender(gender!!, lineResult))
                line = br.readLine()
            }
        } finally {
            br.close() 
        }
        return result
    }

    fun getForms(headers: List<String>) : List<WordForm> {
        val formsHeader = headers.subList(1, headers.size)
        return formsHeader.map { getForm(it) }
    }

    fun getForm(text: String): WordForm {
        val parts = text.split(",")
        return WordForm(valueOf<WordCase>(parts[0])!!, valueOf<Cardinality>(parts[1])!!)
    }
}
