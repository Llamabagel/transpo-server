/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import ca.llamabagel.transpo.Configuration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object DataSource {
    private val config: HikariConfig = HikariConfig()
    private val ds: HikariDataSource

    init {
        config.jdbcUrl = "jdbc:postgresql://${Configuration.SQL_HOST}:${Configuration.SQL_PORT}/${Configuration.SQL_DATABASE}"
        config.username = Configuration.SQL_USER
        config.password = Configuration.SQL_PASSWORD

        ds = HikariDataSource(config)
    }

    fun getConnection(): Connection = ds.connection
}