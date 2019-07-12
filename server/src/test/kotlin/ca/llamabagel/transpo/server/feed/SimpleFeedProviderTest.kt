/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.feed

import ca.llamabagel.transpo.server.utils.CoroutinesDispatcherProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.http.hostWithPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.runBlocking
import org.apache.http.entity.ContentType
import org.junit.Test
import kotlin.test.assertEquals

internal class SimpleFeedProviderTest {
    private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
    private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

    private val mockClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                when (val fullUrl = request.url.fullUrl) {
                    "http://www.octranspo.com/en/feeds/updates-en/" -> {
                        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.TEXT_XML.toString()))
                        respond(
                            "<rss xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:wfw=\"http://wellformedweb.org/CommentAPI/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\" xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\" version=\"2.0\"><channel><title>OC Transpo - Live Updates</title><link>http://www.octranspo.com/</link><description>Daily route changes resulting from detours and cancelled trips are listed here by OC Transpo staff on weekdays, Saturdays until 9 pm and Sundays until 5 pm.</description><atom:link href=\"http://octranspo1.com/feeds/updates-en\" rel=\"self\" type=\"application/rss+xml\"/><item><title>91 Orleans: Cancelled Trip</title><pubDate>Fri, 05 Jul 2019 09:02:00 EDT</pubDate><category><![CDATA[ Cancelled trips ]]></category><category><![CDATA[ affectedRoutes-91 ]]></category><link>https://www.octranspo.com/en/alerts#alert-91-orleans-cancelled-trip-1562331835823</link><description><![CDATA[July 05 2019 - <p>The trip scheduled from Baseline Station at 8:27 to Orleans Station at 09:18 was cancelled. The following trip is 15 minutes later.</p>]]></description><guid>https://www.octranspo.com/en/alerts#alert-91-orleans-cancelled-trip-1562331835823</guid></item><item><title>91 Tunney’s Pasture: Cancelled Trip</title><pubDate>Fri, 05 Jul 2019 08:58:00 EDT</pubDate><category><![CDATA[ Cancelled trips ]]></category><category><![CDATA[ affectedRoutes-91 ]]></category><link>https://www.octranspo.com/en/alerts#alert-91-tunneys-pasture-cancelled-trip-1562331572626</link><description><![CDATA[July 05 2019 - <p>The trip scheduled from Orleans Station at 8:40 to Tunney&#39;s Pasture Station at 09:18 was cancelled. The following trip is 15 minutes later.</p>]]></description><guid>https://www.octranspo.com/en/alerts#alert-91-tunneys-pasture-cancelled-trip-1562331572626</guid></item><item><title>95 Barrhaven Centre: cancelled trip</title><pubDate>Fri, 05 Jul 2019 08:36:00 EDT</pubDate><category><![CDATA[ Cancelled trips ]]></category><category><![CDATA[ affectedRoutes-95 ]]></category><link>https://www.octranspo.com/en/alerts#alert-95-barrhaven-centre-cancelled-trip-1562330478451</link><description><![CDATA[July 05 2019 - <p>The trip scheduled from Trim Station at 8:21 to Minto Rec Centre at 10:00 was cancelled. The following trip is 15 minutes later.</p>]]></description><guid>https://www.octranspo.com/en/alerts#alert-95-barrhaven-centre-cancelled-trip-1562330478451</guid></item><item><title>South Keys Station: Elevator</title><pubDate>Thu, 04 Jul 2019 10:12:00 EDT</pubDate><category><![CDATA[ General Message ]]></category><category><![CDATA[ ]]></category><link>https://www.octranspo.com/en/alerts#alert-south-keys-station-elevator-1</link><description><![CDATA[July 04 2019 - <p><u><strong>Northbound elevator # 37:</strong></u><br /> The northbound elevator at South Keys Station is out of service for unforeseen maintenance. We expect the elevator to be out of service for a few days and regret any inconvenience this may cause.</p> <p>Customers requiring the use of an elevator to access the northbound platform are advised to:use the accessible path between the lower level of the station and the southbound platform., or take Route 6 to the Bank Street side of the South Keys Shopping Centre. </p> <p>For Travel Planning assistance, please call <strong>613-741-4390</strong> or press the free <strong>&ldquo;Info&rdquo;</strong> button on any Bell telephone in the station. For emergency assistance, use the nearest yellow emergency callbox.</p>]]></description><guid>https://www.octranspo.com/en/alerts#alert-south-keys-station-elevator-1</guid></item><item><title>19 St.Laurent: detour due watermain break</title><pubDate>Thu, 04 Jul 2019 09:02:00 EDT</pubDate><category><![CDATA[ Detours ]]></category><category><![CDATA[ affectedRoutes-19 ]]></category><link>https://www.octranspo.com/en/alerts#alert-19-stlaurent-detour-due-watermain-break-1562245700653</link><description><![CDATA[July 04 2019 - <p>Route 19 St. Laurent has been detoured due to watermain break in the area of Queen Marry/Bernard.</p> <p>Stop number 9884 located at the intersection of Queen Mary and Bernard with not be available until further notice.</p> <p>We recommend to go to stop number 4402 located at Queen Mary and Frances or stop number 6696 located at St. Laurent bldv and Convetry Rd.</p>]]></description><guid>https://www.octranspo.com/en/alerts#alert-19-stlaurent-detour-due-watermain-break-1562245700653</guid></item><item><title>O-Train Line 2 closures</title><pubDate>Wed, 03 Jul 2019 15:48:00 EDT</pubDate><category><![CDATA[ General Message ]]></category><category><![CDATA[ affectedRoutes-2 O-Train ]]></category><link>https://www.octranspo.com/en/alerts#alert-o-train-line-2-closures-1562183770519</link><description><![CDATA[July 03 2019 - <p>Line 2 will be <strong>fully closed</strong> for maintenance and construction from <strong>July 15&ndash;21</strong>. Line 2 will then reopen <strong>between Greenboro and Carling only</strong>. Bus route R2 will replace service on the closed portions of Line 2. Service along the full length of Line 2 is scheduled to resume <strong>Monday, Aug. 19 (6 am)</strong>.</p> <div class=\"row\"> <div class=\"col-8 col-md-4\"> <h3>Full closure</h3> <p>O-Train Line 2 will be temporarily replaced by R2 bus service from July 15 to July 21 for annual track maintenance and infrastructure upgrades. This work is required to maintain a safe and reliable service for customers. Line 2 train service between Carling and Greenboro Stations only is scheduled to resume at 6 a.m. on Monday, July 22.</p> <h3>Partial Service</h3> <p>Line 2 will run between Greenboro and Carling Stations only, from July 22 until August 18. Bayview Station will remain closed. This is required so that the Ontario Ministry of Transportation can do preparatory construction for their future Highway 417 overpass replacement. Full Line 2 service is scheduled to resume at 6 a.m. on Monday, August 19.</p> </div> <div class=\"col-8 col-md-4\"> <p><a href=\"http://www.octranspo.com/images/files/maps/line2-summer19-closure.png\"><img alt=\"Map of Line 2 full and partial closure and R2 service\" class=\"fancy_image\" src=\"http://www.octranspo.com/images/files/maps/line2-summer19-closure.png\" style=\"width: 100%;\" /></a></p> </div> </div> <h3>R2 Bus Service</h3> <p>Temporary R2 bus service will run every 15 minutes from each station. Signs at each station will show the way to the bus stops for R2 bus service. During the full closure, buses will run between Bayview and South Keys Stations. During partial service, R2 buses will run between Bayview and Carling Stations. Customers will connect between R2 buses and Line 2 trains at Carling Station.</p> <h3>Connections with R2 bus service at Bayview Station</h3> <p>Bus service at Bayview will be temporarily changed to make connections easier between Transitway bus service and the R2 replacement bus service. Starting on July 15, R2 buses will use Stop 2A, the eastbound Transitway stop on the south side of Albert Street. Also, all westbound Transitway routes will use the bus stop on Scott Street just west of Bayview Road, for a shorter walking connection to and from the R2 stop.</p> <h3>Trip Planning</h3> <p>In addition to R2 bus service, there may be other bus route options that work better for you. Use the <a href=\"http://plan.octranspo.com/plan?culture=en\">Travel Planner</a> to get your options.</p>]]></description><guid>https://www.octranspo.com/en/alerts#alert-o-train-line-2-closures-1562183770519</guid></item></channel></rss>",
                            headers = responseHeaders
                        )
                    }
                    else -> error("Unhandled $fullUrl")
                }
            }
        }
    }

    private val provider =
        SimpleFeedProvider(mockClient, CoroutinesDispatcherProvider(Unconfined, Unconfined, Unconfined))

    @Test
    fun `when feed requested then provider returns full list of items`() = runBlocking {
        val result = provider.getFeed("http://www.octranspo.com/en/feeds/updates-en/")
        assertEquals(6, result.size)
    }
}