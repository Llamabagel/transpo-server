/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

/**
 * A generic server error.
 *
 * @property code The Http status code for the error
 * @property message The associated error message
 */
data class GenericError(val code: Int,
                        val message: String)