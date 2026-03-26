package com.motut.mo.util

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class Announcement(
    val title: String = "",
    val content: String = "",
    val date: String = ""
)

object AnnouncementFetcher {
    private const val ANNOUNCEMENT_URL = "https://motut.net.cn/app/gg.xml"

    suspend fun fetchAnnouncement(): Announcement? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            val url = URL(ANNOUNCEMENT_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                connect()
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.inputStream
                val parser = Xml.newPullParser().apply {
                    setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    setInput(inputStream, null)
                }
                return@withContext parseAnnouncement(parser)
            }
            null
        } catch (e: Exception) {
            null
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }

    private fun parseAnnouncement(parser: XmlPullParser): Announcement? {
        var title = ""
        var content = ""
        var date = ""
        var eventType = parser.eventType
        var currentTag = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                }
                XmlPullParser.TEXT -> {
                    when (currentTag) {
                        "title" -> title = parser.text.trim()
                        "content" -> content = parser.text.trim()
                        "date" -> date = parser.text.trim()
                    }
                }
                XmlPullParser.END_TAG -> {
                    currentTag = ""
                }
            }
            eventType = parser.next()
        }

        return if (title.isNotEmpty() || content.isNotEmpty()) {
            Announcement(title, content, date)
        } else {
            null
        }
    }
}
