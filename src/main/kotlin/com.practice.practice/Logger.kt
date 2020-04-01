package com.practice.practice

import java.util.logging.LogManager

/**
 * @author Stefan Liu
 */
open class Logger {
    protected val log = LogManager.getLogManager().getLogger(this::class.simpleName)
}