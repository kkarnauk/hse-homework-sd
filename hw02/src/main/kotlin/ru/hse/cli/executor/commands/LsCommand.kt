package ru.hse.cli.executor.commands

import ru.hse.cli.Environment
import ru.hse.cli.executor.IOEnvironment
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries

/**
 * Represents the command [ls] which lists directory contents.
 */
class LsCommand : AbstractCommand {
    /**
     * Execute [ls] command with arguments and IO environment [ioEnvironment].
     * @param args if empty lists current working directory contents,
     *             if size is 1 lists contents of directory of 1st argument
     *                  (if relative then resolves using current working directory).
     * @param ioEnvironment stores output and error streams to print a result or error message during execution.
     * @return 0 if execution was successful
     *         1 if given path is not a directory
     *         2 if too many args given.
     */
    override fun execute(args: List<String>, ioEnvironment: IOEnvironment): Int {
        if (args.size > 1) {
            ioEnvironment.errorStream.write("ls: too many arguments (1 or 0 expected)".toByteArray())
            return 2
        }
        val path = if (args.isEmpty()) {
            Environment.workingDirectory
        } else {
            Environment.workingDirectory.resolve(args.first())
        }

        if (!path.isDirectory() && !path.isRegularFile()) {
            ioEnvironment.errorStream.write("ls: cannot find file or directory ${args[0]}".toByteArray())
            return 1
        }
        val contents = if (path.isDirectory()) {
            path.listDirectoryEntries().map { it.fileName }.sorted().joinToString(separator = " ")
        } else {
            args[0]
        }
        ioEnvironment.outputStream.write(contents.toByteArray())
        return 0
    }

}
