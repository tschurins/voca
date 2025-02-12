package jal.voca.app

import jal.voca.lang.Dictionary
import jal.voca.lang.WordCategory

class Favorites {
    private val favorites: MutableSet<String> = mutableSetOf()

    fun clear() {
        favorites.clear()
    }

    fun isEmpty() : Boolean {
        return favorites.isEmpty()
    }

    fun isFavorite(word: String): Boolean {
        return favorites.contains(word)
    }

    fun setFavorite(word: String, favorite: Boolean) {
        if (favorite) {
            favorites.add(word)
        } else {
            favorites.remove(word)
        }
    }

    fun getFavoritesCategory(dico: Dictionary) : WordCategory {
        return WordCategory("Favorites",
            dico.allWords().filter { favorites.contains(it.word.word) }
        )
    }
}