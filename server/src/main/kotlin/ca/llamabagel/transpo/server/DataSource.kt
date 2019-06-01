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
        config.jdbcUrl = "jdbc:postgresql://${Config.SQL_HOST}:${Config.SQL_PORT}/${Config.SQL_DATABASE}"
        config.username = Config.SQL_USER
        config.password = Config.SQL_PASSWORD
        config.leakDetectionThreshold = 2000

        ds = HikariDataSource(config)
    }

    fun getConnection(): Connection = ds.connection
}