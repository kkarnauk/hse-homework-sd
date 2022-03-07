package ru.hse.cli.executor.commands

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.hse.cli.Environment
import ru.hse.cli.executor.BaseExecutorTest
import ru.hse.cli.executor.IOEnvironment
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

internal class CdCommandTest : BaseExecutorTest() {

    @BeforeEach
    fun resetEnvironment() {
        Environment.resetDirectory()
    }

    private fun runInTestDirectory(arguments: List<String>): IOEnvironment {
        val dir = createTempDirectory().toFile()
        File(dir, "file1").createNewFile()
        File(dir, "emptyDir").also { it.mkdir() }
        val subdir = File(dir, "subdir").also { it.mkdir() }
        File(subdir, "file2").createNewFile()

        val cdCommand = CdCommand()
        val inputStream = ByteArrayInputStream(ByteArray(0))
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val ioEnvironment = IOEnvironment(inputStream, outputStream, errorStream)

        Environment.resolveDirectory(dir.toPath())

        cdCommand.execute(arguments, ioEnvironment)
        return ioEnvironment
    }

    @Test
    fun executeNoArgs() {
        val defaultEnvironment = Environment.workingDirectory
        runInTestDirectory(emptyList())
        assertEquals(defaultEnvironment, Environment.workingDirectory)
    }

    @Test
    fun executeDirArg() {
        runInTestDirectory(listOf("subdir"))
        assertEquals("subdir", Environment.workingDirectory.name)
    }

    @Test
    fun executeCdChangeDirectoryForExternalCommand() {
        runInTestDirectory(listOf("emptyDir"))
        ExternalCommand().execute(
            listOf("touch", "wow"), IOEnvironment(
                ByteArrayInputStream(ByteArray(0)),
                ByteArrayOutputStream(),
                ByteArrayOutputStream(),
            )
        )
        assertEquals("emptyDir", Environment.workingDirectory.name)
        assertArrayEquals(arrayOf("wow"), Environment.workingDirectory.toFile().list())
    }

    @Test
    fun executeFailureFileArg() {
        val ioEnvironment = runInTestDirectory(listOf("file1"))
        assertEquals(listOf("cd: cannot find directory file1"), ioEnvironment.errorStream.toString().lines())
    }

    @Test
    fun executeFailureDirectoryNotExist() {
        val ioEnvironment = runInTestDirectory(listOf("fake"))
        assertEquals("", ioEnvironment.outputStream.toString())
        assertEquals(listOf("cd: cannot find directory fake"), ioEnvironment.errorStream.toString().lines())
    }

    @Test
    fun executeFailureToManyArgs() {
        val ioEnvironment = runInTestDirectory(listOf("emptyDir", "subdir"))
        assertEquals("", ioEnvironment.outputStream.toString())
        assertEquals(listOf("cd: too many arguments"), ioEnvironment.errorStream.toString().lines())
    }
}
