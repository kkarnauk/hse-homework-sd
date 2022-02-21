package ru.hse.cli.executor.commands

import ru.hse.cli.Environment
import ru.hse.cli.executor.IOEnvironment

/**
 * Represents the command [pwd] which prints the contents of a file.
 */
class PwdCommand: AbstractCommand {
    /**
     * Execute [pwd] command with arguments and IO environment [ioEnvironment].
     * @param args should be ignored.
     * @param ioEnvironment stores output and error streams to print a result or error message during execution.
     * @return 0 as a mark that execution was successful.
     */
    override fun execute(args: List<String>, ioEnvironment: IOEnvironment): Int {
        val path = Environment.workingDirectory.toString()
        ioEnvironment.inputStream.reset()
        ioEnvironment.outputStream.write(path.toByteArray())

        return 0
    }
}
