package com.alfaazplus.sunnah.db.converters

import androidx.room3.TypeConverter
import com.alfaazplus.sunnah.db.entities.v2.CollectionType
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeType
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceType

class HadithDbConverters {
    @TypeConverter
    fun valueToGrade(value: String): HadithGradeType = HadithGradeType.fromValue(value)

    @TypeConverter
    fun valueFromGrade(grade: HadithGradeType): String = grade.type

    @TypeConverter
    fun valueToReferenceType(value: String): HadithReferenceType = HadithReferenceType.fromValue(value)

    @TypeConverter
    fun valueFromReferenceType(type: HadithReferenceType): String = type.type

    @TypeConverter
    fun valueToCollectionType(value: String): CollectionType = CollectionType.fromType(value)

    @TypeConverter
    fun valueFromCollectionType(type: CollectionType): String = type.type
}
