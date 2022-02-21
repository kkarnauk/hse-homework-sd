package ru.hse.cli.executor.commands

import ru.hse.cli.Environment
import ru.hse.cli.executor.IOEnvironment
import java.nio.file.Path

/**
 * Represents the command [cd] which changes working directory.
 */
class CdCommand : AbstractCommand {
    /**
     * Execute [cd] command with arguments and IO environment [ioEnvironment].
     * @param args if empty resets working directory to initial one,
     *             if size is 1 changes working directory to directory of first argument
     *                  (if relative then resolves using current working directory).
     * @param ioEnvironment stores output and error streams to print a result or error message during execution.
     * @return 0 if execution was successful
     *         1 if too many args given.
     */
    override fun execute(args: List<String>, ioEnvironment: IOEnvironment): Int {
        return when (args.size) {
            0 -> Environment.resetDirectory().let { 0 }
            1 -> Environment.resolveDirectory(Path.of(args[0])).let { 0 }
            else -> ioEnvironment.errorStream.write("cd: too many arguments".toByteArray()).let { 1 }
        }
    }
}
