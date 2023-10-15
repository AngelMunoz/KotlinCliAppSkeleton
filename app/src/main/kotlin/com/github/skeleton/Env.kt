package com.github.skeleton

import com.github.skeleton.services.localFsFactory
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.kodein.di.*

fun getAppContainer(): DI {
    return DI {
        bindInstance { localFsFactory() }
        bindMultiton<String, KLogger> { name: String -> KotlinLogging.logger(name) }
    }
}
