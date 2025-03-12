package jal.voca.app.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class VocaDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "voca.db"

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(FavoriteTableContract.FavoriteTable.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

}

class FavoriteTableContract {
    object FavoriteTable : BaseColumns {
        private const val TABLE_NAME = "favorite"
        private const val COLUMN_NAME_WORD = "word"

        const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME_WORD TEXT PRIMARY KEY)"

        fun addFavorite(db: SQLiteDatabase, word: String) {
            val values = ContentValues().apply {
                put(COLUMN_NAME_WORD, word)
            }
            db.insert(TABLE_NAME, null, values)
        }

        fun removeFavorite(db: SQLiteDatabase, word: String) {
            val selection = "$COLUMN_NAME_WORD LIKE ?"
            val selectionArgs = arrayOf(word)
            val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)
        }

        fun getFavorites(db: SQLiteDatabase): List<String> {
            // Define a projection that specifies which columns to return
            val projection = arrayOf(COLUMN_NAME_WORD)

            val cursor = db.query(
                TABLE_NAME,         // The table to query
                projection,         // The array of columns to return (pass null to get all)
                null,      // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                null,       // don't group the rows
                null,        // don't filter by row groups
                null        // The sort order
            )

            val words = mutableListOf<String>()
            with(cursor) {
                while (moveToNext()) {
                    val word = getString(getColumnIndexOrThrow(COLUMN_NAME_WORD))
                    words.add(word)
                }
            }
            cursor.close()
            return words
        }
    }
}