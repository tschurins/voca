package jal.voca.lang

class WordParts {
    val all: List<String>
    val comment: String

    constructor(word: String) {
        val noComment: String
        val startComment = word.indexOf("(")
        if (startComment >= 0) {
            comment = " " + word.substring(startComment).trim()
            noComment = word.substring(0, startComment)
        } else {
            comment = ""
            noComment = word
        }

        all = noComment.split("/").map { it.trim() }
    }

    constructor(all: List<String>, comment: String) {
        this.all = all
        this.comment = comment
    }

    override fun toString() : String {
        return noComment() + comment
    }

    fun noComment() : String {
        return all.joinToString(" / ")
    }

    fun map(wordFunction: (String) -> String) : WordParts {
        return WordParts(
            all.map(wordFunction),
            comment,
        )
    }
}