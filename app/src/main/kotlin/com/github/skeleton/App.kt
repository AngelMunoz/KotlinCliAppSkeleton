/*
 * The main entry point for our app, here we can customize and orchestrate
 * our application's environment and the commands that will be available
 */
package com.github.skeleton

import com.github.skeleton.cli_options.ShowDirectoryItems
import com.github.skeleton.handlers.gatherFsItems
import picocli.CommandLine
import kotlin.system.exitProcess


fun main(vararg argv: String) {

    // The App Environment is the DI container for the application
    val appEnv = getAppContainer()
    val mainCmd = ShowDirectoryItems(appEnv, ::gatherFsItems)

    exitProcess(CommandLine(mainCmd).execute(*argv))
}
