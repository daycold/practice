package com.practice.practice

import java.util.logging.LogManager
import java.util.logging.Logger

/**
 * @author Stefan Liu
 */
open class Logger {
    protected val log: Logger = LogManager.getLogManager().getLogger(this::class.simpleName)
}