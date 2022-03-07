package ru.hse.cli.executor.commands

import ru.hse.cli.Environment
import ru.hse.cli.exception.UnknownCommandException
import ru.hse.cli.executor.IOEnvironment
import java.io.File
import java.io.IOException

/**
 * Represents a call of external command.
 */
class ExternalCommand : AbstractCommand {

    /**
     * Execute external command with arguments [args] and IO environment [ioEnvironment].
     * Execution can be unsuccessful if at least one file doesn't exitst.
     * @param args file names to process.
     * @param ioEnvironment stores output and error streams to print a result or error message during execution.
     * @return 0 if execution was successful, -1 otherwise.
     */
    override fun execute(args: List<String>, ioEnvironment: IOEnvironment): Int {
        ioEnvironment.inputStream.reset()

        val processBuilder = ProcessBuilder(args)
        val env: MutableMap<String, String> = processBuilder.environment()

        for ((variable, value) in Environment.vars) {
            env[variable] = value
        }

        processBuilder.directory(Environment.workingDirectory.toFile())

        val process: Process =
            try {
                processBuilder.start()
            } catch (e: IOException) {
                throw UnknownCommandException(args[0])
            }

        ioEnvironment.inputStream.transferTo(process.outputStream)
        process.waitFor()

        process.inputStream.transferTo(ioEnvironment.outputStream)
        process.errorStream.transferTo(ioEnvironment.errorStream)

        return if (ioEnvironment.errorStream.toString().isEmpty()) {
            0
        } else {
            -1
        }
    }

}
