/*
 * Copyright (c) 2020 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

/**
 * Object representation of the config.properties file.
 * All properties are loaded and read when this object is initialized.
 */
class Configuration(val root: String) {
    val SQL_USER: String
    val SQL_HOST: String
    val SQL_PORT: String
    val SQL_PASSWORD: String
    val SQL_DATABASE: String
    val DATA_PACKAGE_DIRECTORY: String

    init {
        if (!Files.exists(Paths.get("$root/config.properties"))) {
            throw FileNotFoundException("config.properties file not found.")
        }

        val properties = Properties().apply {
            load(FileInputStream("$root/config.properties"))
        }

        SQL_USER =
            properties.getProperty("SQL_USER") ?: throw IllegalStateException("SQL_USER not set in config.properties")
        SQL_HOST =
            properties.getProperty("SQL_HOST") ?: throw IllegalStateException("SQL_HOST not set in config.properties")
        SQL_PORT =
            properties.getProperty("SQL_PORT") ?: throw IllegalStateException("SQL_PORT not set in config.properties")
        SQL_PASSWORD = properties.getProperty("SQL_PASSWORD")
            ?: throw IllegalStateException("SQL_PASSWORD not set in config.properties")
        SQL_DATABASE = properties.getProperty("SQL_DATABASE") ?: "transit"
        DATA_PACKAGE_DIRECTORY = properties.getProperty("DATA_PACKAGE_DIRECTORY")?.removeSuffix("/")
            ?: throw IllegalStateException("DATA_PACKAGE_DIRECTORY not set in config.properties")
    }

    /**
     * Gets an SQL Connection to the SQL server specified in the configuration.
     * @return An SQL connection, or null if the connection failed.
     */
    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection("jdbc:postgresql://$SQL_HOST:$SQL_PORT/$SQL_DATABASE", SQL_USER, SQL_PASSWORD)
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}