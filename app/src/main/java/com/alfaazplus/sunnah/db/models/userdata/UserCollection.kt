package com.alfaazplus.sunnah.db.models.userdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionContract
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionContract.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

@Entity(
    tableName = UserCollectionContract.TABLE_NAME,
)
data class UserCollection(
    @ColumnInfo(name = Columns.ID) @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = Columns.NAME) val name: String,
    @ColumnInfo(name = Columns.DESCRIPTION) val description: String? = null,
    @ColumnInfo(name = Columns.COLOR) val color: String? = null,
    @ColumnInfo(name = Columns.CREATED_AT) val createdAt: Date = Date(),
    @ColumnInfo(name = Columns.UPDATED_AT) val updatedAt: Date = Date(),
) {

    @Ignore
    var itemsCount: Flow<Int> = MutableStateFlow(0)

    override fun toString(): String {
        return "UserCollection(id=$id, name='$name', description=$description, color=$color, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
