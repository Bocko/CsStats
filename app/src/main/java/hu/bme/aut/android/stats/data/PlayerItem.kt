package hu.bme.aut.android.stats.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playeritem")
data class PlayerItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,

    @ColumnInfo(name = "persona_name") var persona_name: String,
    @ColumnInfo(name = "steam_id") var steam_id: Long,
    @ColumnInfo(name = "pic_url") var pic_url: String
)