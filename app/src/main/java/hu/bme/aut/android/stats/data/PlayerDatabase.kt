package hu.bme.aut.android.stats.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlayerItem::class], version = 1)
abstract class PlayerDatabase : RoomDatabase() {
    companion object {
        fun getDatabase(applicationContext: Context): PlayerDatabase {
            return Room.databaseBuilder(
                applicationContext,
                PlayerDatabase::class.java,
                "shopping-list"
            ).build();
        }
    }

    abstract fun shoppingItemDao(): PlayerItemDao
}