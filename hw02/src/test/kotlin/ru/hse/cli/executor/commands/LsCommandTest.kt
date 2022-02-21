package ru.hse.cli.executor.commands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.hse.cli.Environment
import ru.hse.cli.executor.BaseExecutorTest
import ru.hse.cli.executor.IOEnvironment
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.io.path.createTempDirectory

internal class LsCommandTest : BaseExecutorTest() {

    private fun runInTestDirectory(arguments: List<String>): IOEnvironment {
        val dir = createTempDirectory().toFile()
        File(dir, "file1").createNewFile()
        File(dir, "file2").createNewFile()
        val subdir = File(dir, "subdir").also { it.mkdir() }
        File(subdir, "file3").createNewFile()

        val lsCommand = LsCommand()
        val inputStream = ByteArrayInputStream(ByteArray(0))
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val ioEnvironment = IOEnvironment(inputStream, outputStream, errorStream)

        Environment.resolveDirectory(dir.toPath())

        lsCommand.execute(arguments, ioEnvironment)
        return ioEnvironment
    }

    @Test
    fun executeNoArgs() {
        val ioEnvironment = runInTestDirectory(emptyList())
        assertEquals(listOf("file1", "file2", "subdir"), ioEnvironment.outputWords)
    }

    @Test
    fun executeFileArg() {
        val ioEnvironment = runInTestDirectory(listOf("file1"))
        assertEquals(listOf("file1"), ioEnvironment.outputWords)
    }

    @Test
    fun executeDirArg() {
        val ioEnvironment = runInTestDirectory(listOf("subdir"))
        assertEquals(listOf("file3"), ioEnvironment.outputWords)
    }

    @Test
    fun executeFailureFileNotExist() {
        val ioEnvironment = runInTestDirectory(listOf("fake"))
        assertEquals("", ioEnvironment.output)
        assertEquals(listOf("TODO"), ioEnvironment.errorStream.toString().lines())
    }

    @Test
    fun executeFailureToManyArgs() {
        val ioEnvironment = runInTestDirectory(listOf("file1", "file2"))
        assertEquals("", ioEnvironment.output)
        assertEquals(listOf("TODO"), ioEnvironment.errorStream.toString().lines())
    }

    private val IOEnvironment.outputWords get() = output.split(' ')
    private val IOEnvironment.output get() = outputStream.toString()
}
