package ru.hse.cli.executor

import java.io.File
import java.io.FileNotFoundException

/**
 * Represents the command [cat] which returns the contents of a file.
 */
class CatCommand : AbstractCommand() {

    private fun getData(fileName: String): ByteArray {
        return File(fileName).readBytes()
    }

    /**
     * Execute [cat] command with arguments [args] and IO environment [ioEnvironment].
     * Execution can be unsuccessful if at least one file doesn't exitst.
     * @param args file names to process.
     * @param ioEnvironment stores output and error streams to print a result or error message during execution.
     * @return 0 if execution was successful, -1 otherwise.
     */
    override fun execute(args: List<String>, ioEnvironment: IOEnvironment): Int {
        var result = 0

        args.forEach {
            try {
                ioEnvironment.outputStream.write(getData(it))
            } catch (e: FileNotFoundException) {
                ioEnvironment.errorStream.write("File '$it' does not exist!".toByteArray())
                result = -1
            }
        }

        return result
    }
}
