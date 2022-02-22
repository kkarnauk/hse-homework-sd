package ru.hse.cli.executor.commands

import kotlinx.cli.*
import org.apache.commons.io.IOUtils
import ru.hse.cli.executor.IOEnvironment
import java.io.File
import java.lang.Integer.max
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Parser for arguments of the GrepCommand.
 * @key [-w] (word-regexp) - search by whole words.
 * @key [-i] (ignore-case) - case-insensitive search.
 * @key [-A NUM] (after-context) - prints NUM strings after matched string.
 */
class GrepParser {
    val parser = ArgParser("")
    val needle by parser.argument(ArgType.String, description = "Needle")
    val inputFile by parser.argument(ArgType.String, description = "Input file").optional()
    val wordRegexp by parser.option(
        ArgType.Boolean,
        fullName = "word-regexp",
        shortName = "w",
        description = "Search by whole words"
    )
        .default(false)
    val ignoreCase by parser.option(
        ArgType.Boolean,
        fullName = "ignore-case",
        shortName = "i",
        description = "Case-insensitive search"
    )
        .default(false)
    val afterContext by parser.option(
        ArgType.Int,
        fullName = "after-context",
        shortName = "A",
        description = "Very hard to explain"
    ).default(0)

    fun parse(args: List<String>) {
        parser.parse(args.toTypedArray())
        return
    }
}

/**
 * Represents the command [grep]
 */
class GrepCommand : AbstractCommand {

    /**
     * Execute grep command with arguments [args] and IO environment [ioEnvironment].
     * Execution can be unsuccessful if at least one file doesn't exitst.
     * @param args arguments to process.
     * @param ioEnvironment stores output and error streams to print a result or error message during execution.
     * @return 0 if execution was successful, -1 otherwise.
     */
    override fun execute(args: List<String>, ioEnvironment: IOEnvironment): Int {
        val grepParser = GrepParser()
        grepParser.parse(args)

        var needle = grepParser.needle.toRegex()
        val content: List<String> = if (grepParser.inputFile == null) {
            IOUtils.toString(ioEnvironment.inputStream, StandardCharsets.UTF_8).split("\n")
        } else {
            val file = File(grepParser.inputFile!!)
            file.readLines()
        }

        var matchResults: MutableList<String> = content.toMutableList()

        if (grepParser.ignoreCase) {
            needle = grepParser.needle.lowercase(Locale.getDefault()).toRegex()
            matchResults = content.filter {
                needle.find(it.lowercase(Locale.getDefault())) != null
            }.toMutableList()
        }

        if (!grepParser.ignoreCase && !grepParser.wordRegexp) {
            matchResults = matchResults.filter {
                needle.find(it) != null
            }.toMutableList()
        }

        if (grepParser.wordRegexp) {
            needle = "\\b${needle.pattern}\\b".toRegex()
            matchResults = matchResults.filter {
                needle.find(if (grepParser.ignoreCase) it.lowercase(Locale.getDefault()) else it) != null
            }.toMutableList()
        }

        if (grepParser.afterContext != 0) {
            var matchedIndex = 0
            val segments: MutableList<Pair<Int, Int>> = mutableListOf()

            for (i in content.indices) {
                if (content[i] == matchResults[matchedIndex]) {
                    segments.add(Pair(i, i + grepParser.afterContext))
                    matchedIndex++
                }

                if (matchedIndex == matchResults.size)
                    break
            }

            val processedIndex = processSegments(segments)
            matchResults = content.filterIndexed {index, str -> index in processedIndex}.toMutableList()
        }

        ioEnvironment.outputStream.write(matchResults.joinToString("\n").toByteArray())

        return 0
    }

    private fun processSegments(segments: MutableList<Pair<Int, Int>>): List<Int> {
        val matchResults = mutableListOf<Int>()
        var currentRightBorder = -1
        for (i in segments.indices) {
            val segment = segments[i]

            if (segment.first > currentRightBorder) {
                for (ind in segment.first..segment.second)
                    matchResults.add(ind)
            } else {
                for (ind in segment.first .. max(currentRightBorder, segment.second)) {
                    matchResults.add(ind)
                }
            }

            currentRightBorder = max(currentRightBorder, segment.second)
        }

        return matchResults
    }
}
