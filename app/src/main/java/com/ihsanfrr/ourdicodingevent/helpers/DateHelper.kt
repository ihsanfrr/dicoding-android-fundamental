package com.ihsanfrr.ourdicodingevent.helpers

import java.text.SimpleDateFormat
import java.util.Locale

object DateHelper {
    fun format(input: String): String? {
        val originFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val expectFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())

        val date = originFormat.parse(input)
        return date?.let { expectFormat.format(it) }
    }
}