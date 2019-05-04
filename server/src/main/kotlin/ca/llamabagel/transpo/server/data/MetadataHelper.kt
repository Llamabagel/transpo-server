/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.data

import ca.llamabagel.transpo.models.app.AppMetadata
import ca.llamabagel.transpo.models.app.Version
import ca.llamabagel.transpo.server.DataSource

fun getMetadata(platform: String): AppMetadata? {
    val connection = DataSource.getConnection()
    val statement = connection.prepareStatement("SELECT * FROM metadata WHERE platform = ?")
        .apply {
            setString(1, platform)
        }

    val queryResult = statement.executeQuery()
    return if (queryResult.next()) {
        val dataVersion = queryResult.getString("data_version")
        val schemaVersion = queryResult.getInt("schema_version")
        val appVersionCode = queryResult.getString("app_version_code")

        AppMetadata(Version(dataVersion), schemaVersion, appVersionCode)
    } else {
        null
    }
}