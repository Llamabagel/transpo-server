/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Object to access various API keys configured in the project setup.
 * The various keys are loaded in from the keys.properties file located in the server module.
 *
 * @property OC_TRANSPO_APP_ID The OC Transpo app id required to access their API.
 * @property OC_TRANSPO_API_KEY The API key associated with the App id used to access the OC Transpo API.
 */
object Keys {
    val OC_TRANSPO_APP_ID: String
    var OC_TRANSPO_API_KEY: String
    var GOOGLE_API_KEY: String

    init {
        if (!Files.exists(Paths.get("server/keys.properties"))) {
            throw IOException("keys.properties file not found.")
        }

        val properties = Properties().apply {
            load(FileInputStream("server/keys.properties"))
        }

        if (!properties.contains("OC_TRANSPO_APP_ID")) {
            OC_TRANSPO_APP_ID = properties.getProperty("OC_TRANSPO_APP_ID")
        } else {
            throw IllegalStateException("OC Transpo App Id not set in keys.properties file.")
        }

        if (!properties.contains("OC_TRANSPO_API_KEY")) {
            OC_TRANSPO_API_KEY = properties.getProperty("OC_TRANSPO_API_KEY")
        } else {
            throw IllegalStateException("OC Transpo API key not set in keys.properties file.")
        }

        if (!properties.contains("GOOGLE_API_KEY")) {
            GOOGLE_API_KEY = properties.getProperty("GOOGLE_API_KEY")
        } else {
            throw IllegalStateException("Google API key not set in keys.properties")
        }
    }
}