package com.github.skeleton

import com.github.skeleton.cli_options.ShowDirectoryItems
import com.github.skeleton.handlers.gatherFsItems
import com.github.skeleton.types.IFileSystem
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindInstance
import picocli.CommandLine
import kotlin.test.Test


/**
 * this is a factory function that provides us with a new environment with
 * fake implementations of our dependencies, this will allow us to control
 * side effectual operations and assert on them like reading from disk
 * or logging to the console
 */
fun makeTestEnvironment(stdout: MutableList<String>, stderr: MutableList<String>): DI {
    return DI {
        bindInstance {
            object : IFileSystem {
                override fun listItems(path: String): List<String> {
                    return listOf("/dir1", "/file1", "/file2")
                }

                override fun walkDown(path: String): List<String> {
                    return listOf("/file1", "/dir1/file1", "/dir2/file1", "/dir2/dir1/file1")
                }
            }
        }
        bindFactory<String, KLogger> {  loggerName ->
            object : KLogger {
                override val name: String
                    get() = loggerName

                override fun at(level: Level, marker: Marker?, block: KLoggingEventBuilder.() -> Unit) {
                    val evb = KLoggingEventBuilder()
                    evb.block()
                    when (level) {
                        Level.ERROR -> stderr.add(evb.message.orEmpty())
                        else -> stdout.add(evb.message.orEmpty())
                    }
                }

                override fun isLoggingEnabledFor(level: Level, marker: Marker?): Boolean = true
            }
        }
    }
}

/**
 * These E2E tests are meant to test command line options and their interactions
 * with our provided environment and fake services
 * each test should arrange a new environment, register the necessary commands, subcommands
 * and options and then execute the command line
 */
class E2ETests {

    @Test
    fun showDirectoryItemsNonRecursive() {

        val stdoutSink = mutableListOf<String>()
        val stderrSink = mutableListOf<String>()

        val testEnv = makeTestEnvironment(stdoutSink, stderrSink)

        val cli = CommandLine(ShowDirectoryItems(testEnv, ::gatherFsItems))
        // act
        val result = cli.execute("-p", "./")
        assert(result == 0)
        assert(stdoutSink.size == 3)
        assert(stdoutSink[1].contains("/file1"))
    }

    @Test
    fun showDirectoryItemsRecursive() {

        val stdoutSink = mutableListOf<String>()
        val stderrSink = mutableListOf<String>()

        val testEnv = makeTestEnvironment(stdoutSink, stderrSink)

        val cli = CommandLine(ShowDirectoryItems(testEnv, ::gatherFsItems))
        // act
        val result = cli.execute("-p", "./", "-r")
        assert(result == 0)
        assert(stdoutSink.size == 4)
        assert(stdoutSink[1].contains("/dir1/file1"))
    }
}
