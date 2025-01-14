package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

class ArticlesCsvReader {
    fun readArticles(formStream: InputStream) : List<ArticlesPerGender> {
        val br = BufferedReader(formStream.reader())
        val result: MutableList<ArticlesPerGender> = mutableListOf()
        try {
            var line: String? = br.readLine()
            val headers = line!!.split("|")
            val forms = getForms(headers)
            line = br.readLine()
            while (line != null) {
                val lineResult: MutableMap<WordForm, String> = mutableMapOf()
                val values = line.split("|")
                val gender = valueOf<Gender>(values[0])
                val articleType = valueOf<ArticleType>(values[1])
                for (i in 2..<values.size) {
                    val form = forms[i - 2]
                    lineResult[form] = values[i]
                }
                result.add(ArticlesPerGender(gender!!, articleType!!, lineResult))
                line = br.readLine()
            }
        } finally {
            br.close() 
        }
        return result
    }

    fun getForms(headers: List<String>) : List<WordForm> {
        val formsHeader = headers.subList(2, headers.size)
        return formsHeader.map { getForm(it) }
    }

    fun getForm(text: String): WordForm {
        val parts = text.split(",")
        return WordForm(valueOf<WordCase>(parts[0])!!, valueOf<Cardinality>(parts[1])!!)
    }
}
