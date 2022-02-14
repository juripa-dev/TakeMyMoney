package com.juripa.takemymoney

import org.joda.time.DateTime
import org.joda.time.Days

object TimeUtil {

    fun isOverToday(beforeTime: String): Boolean {
        val days = Days.daysBetween(
            DateTime.parse(beforeTime).toLocalDate(),
            DateTime.now().toLocalDate()
        ).days

        return days == 0
    }

    fun nowToString(): String = DateTime.now().toString()
}