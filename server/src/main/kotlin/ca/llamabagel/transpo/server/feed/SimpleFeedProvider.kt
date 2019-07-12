/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

@file:Suppress("BlockingMethodInNonBlockingContext")

package ca.llamabagel.transpo.server.feed

import ca.llamabagel.transpo.models.updates.LiveUpdate
import ca.llamabagel.transpo.server.utils.CoroutinesDispatcherProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.readBytes
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class SimpleFeedProvider(
    private val client: HttpClient = HttpClient(Apache),
    private val dispatchers: CoroutinesDispatcherProvider = CoroutinesDispatcherProvider()
) : LiveUpdateFeedProvider {
    private val xPath = XPathFactory.newInstance().newXPath()

    override suspend fun getFeed(feedUrl: String): List<LiveUpdate> = withContext(dispatchers.io) {
        val response = client.call(feedUrl)
            .response
            .readBytes()

        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(ByteArrayInputStream(response))

        val feedRoot = xPath.evaluate(
            "/rss/channel",
            document,
            XPathConstants.NODE
        ) as? Element

        val items = xPath.evaluate("./item", feedRoot, XPathConstants.NODESET) as NodeList
        val results = mutableListOf<LiveUpdate>()
        for (i in 0 until items.length) {
            results.add(buildUpdateFromElement(items.item(i) as Element))
        }

        return@withContext results
    }

    /**
     * Parses the XML in the given [element] and builds a LiveUpdate object containing all of the info from the item.
     * This function will also handle the parsing of the content to determine the featured image, and affected stops.
     */
    private fun buildUpdateFromElement(element: Element): LiveUpdate {
        val title = xPath.evaluate("./title", element, XPathConstants.STRING) as String
        val dateString = xPath.evaluate("./pubDate", element, XPathConstants.STRING) as String
        val date = SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz").parse(dateString)

        val category = xPath.evaluate("./category", element, XPathConstants.STRING) as String
        val guid = xPath.evaluate("./guid", element, XPathConstants.STRING) as String
        val link = xPath.evaluate("./link", element, XPathConstants.STRING) as String
        val description = xPath.evaluate("./description", element, XPathConstants.STRING) as String

        return LiveUpdate(title, date, category, link, description, guid)
    }
}