package com.alfaazplus.sunnah.db.models.scholars

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.ScholarContract
import com.alfaazplus.sunnah.db.contracts.ScholarContract.Columns

@Entity(tableName = ScholarContract.TABLE_NAME)
data class Scholar(
    @ColumnInfo(name = Columns.ID) @PrimaryKey val id: Int,
    @ColumnInfo(name = Columns.STATUS) val status: Int?,
    @ColumnInfo(name = Columns.SHORT_NAME) val shortName: String?,
    @ColumnInfo(name = Columns.FULL_NAME) val fullName: String?,
    @ColumnInfo(name = Columns.ARABIC) val arabic: String?,
    @ColumnInfo(name = Columns.FATHER_NAME) var fatherName: String?,
    @ColumnInfo(name = Columns.MOTHER_NAME) var motherName: String?,
    @ColumnInfo(name = Columns.BIO) val bio: String?,
    @ColumnInfo(name = Columns.CITY) val city: String?,
    @ColumnInfo(name = Columns.RANK) val rank: Int?,
    @ColumnInfo(name = Columns.BIRTH_DATE) val birthDate: String?,
    @ColumnInfo(name = Columns.BIRTH_PLACE) val birthPlace: String?,
    @ColumnInfo(name = Columns.DEATH_DATE) val deathDate: String?,
    @ColumnInfo(name = Columns.DEATH_PLACE) val deathPlace: String?,
    @ColumnInfo(name = Columns.DEATH_CAUSE) val deathCause: String?,
    @ColumnInfo(name = Columns.SIBLINGS) var siblings: String?,
    @ColumnInfo(name = Columns.SPOUSES) var spouses: String?,
    @ColumnInfo(name = Columns.CHILDREN) var children: String?,
    @ColumnInfo(name = Columns.TEACHERS) var teachers: String?,
    @ColumnInfo(name = Columns.STUDENTS) var students: String?,
    @ColumnInfo(name = Columns.INTERESTS) val interests: String?,
    @ColumnInfo(name = Columns.KUNYA) val kunya: String?,
)
