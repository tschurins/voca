package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

fun main(args: Array<String>) {
    val words: List<CategorizedTranslation>
    val wordForms: WordForms

    println("reading google sheet")
    val reader = DictionaryGoogleSheetReader.greek()

    val dirName = if (args.size > 0) args[0] else "./"
    val dir = File(dirName)
    println("writing to " + dir.absolutePath)
    AllCsvWriter().writeAll(reader, dir, "greek")
}
