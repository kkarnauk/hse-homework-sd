package ru.hse.cli.executor.commands

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.hse.cli.Environment
import ru.hse.cli.executor.BaseExecutorTest
import ru.hse.cli.executor.IOEnvironment
import java.io.*

internal class PwdCommandTest: BaseExecutorTest() {

    @Test
    fun testExecuteResult() {
        val pwdCommand = PwdCommand()
        val inputStream = ByteArrayInputStream(ByteArray(0))
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val ioEnvironment = IOEnvironment(inputStream, outputStream, errorStream)
        assertEquals(0, pwdCommand.execute(listOf(), ioEnvironment))
    }

    @Test
    fun testExecuteOutput() {
        val pwdCommand = PwdCommand()
        val expected = Environment.workingDirectory
        val inputStream = ByteArrayInputStream(ByteArray(0))
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val ioEnvironment = IOEnvironment(inputStream, outputStream, errorStream)
        pwdCommand.execute(listOf(), ioEnvironment)
        assertEquals(expected, ioEnvironment.outputStream.toString())
    }

    @Test
    fun testExecuteError() {
        val pwdCommand = PwdCommand()
        val inputStream = ByteArrayInputStream(ByteArray(1))
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val ioEnvironment = IOEnvironment(inputStream, outputStream, errorStream)
        pwdCommand.execute(listOf(), ioEnvironment)
        assertTrue(ioEnvironment.errorStream.toString().isEmpty())
    }
}
