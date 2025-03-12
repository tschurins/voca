package jal.voca.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import jal.voca.app.db.FavoriteTableContract
import jal.voca.app.db.VocaDbHelper
import jal.voca.lang.Dictionary
import jal.voca.lang.WordCategory

class Favorites {
    private val favorites: MutableSet<String> = mutableSetOf()

    fun clear() {
        favorites.clear()
    }

    fun isEmpty(): Boolean {
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

    fun setFavorites(words: Collection<String>) {
        favorites.addAll(words)
    }

    fun getFavoritesCategory(dico: Dictionary): WordCategory {
        return WordCategory("Favorites",
            dico.allWords().filter { favorites.contains(it.word.word) }
        )
    }
}

class FavoritesManager(context: Context) {
    private val dbHelper = VocaDbHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun addFavorite(word: String) {
        globalFavorites.setFavorite(word, true)
        FavoriteTableContract.FavoriteTable.addFavorite(db, word)
    }

    fun removeFavorite(word: String) {
        globalFavorites.setFavorite(word, false)
        FavoriteTableContract.FavoriteTable.removeFavorite(db, word)
    }

    fun close() {
        dbHelper.close()
    }
}

class FavoritesLoader(private val context: Context) {
    fun loadFavorites() {
        val dbHelper = VocaDbHelper(context)
        val db = dbHelper.readableDatabase
        val favorites = FavoriteTableContract.FavoriteTable.getFavorites(db)
        globalFavorites.setFavorites(favorites)
        dbHelper.close()
    }
}
