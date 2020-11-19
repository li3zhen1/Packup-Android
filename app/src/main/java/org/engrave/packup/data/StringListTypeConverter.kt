package org.engrave.packup.data

import androidx.room.TypeConverter

class StringListTypeConverter{

    @TypeConverter
    fun stringListToString(strings: List<String>)= strings.joinToString(LIST_STRING_CONVERT_DELIMITER)

    @TypeConverter
    fun stringToStringList(string: String) = string.split(LIST_STRING_CONVERT_DELIMITER)

    companion object{
        const val LIST_STRING_CONVERT_DELIMITER = ","
    }
}