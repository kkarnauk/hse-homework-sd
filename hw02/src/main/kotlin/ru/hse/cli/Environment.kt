package ru.hse.cli

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Represents static storage that stores environment's state and variables.
 */
object Environment {
    /**
     * Represent current working directory
     */
    var workingDirectory: Path = Paths.get(System.getProperty("user.dir"))
        private set

    /**
     * Stores variables with its values
     */
    val vars: HashMap<String, String> = hashMapOf()

    /**
     * Is CLI shutdowned i.e exit command was executed
     */
    var isShutdowned: Boolean = false

    fun put(variable: String, value: String) {
        vars[variable] = value
    }

    /**
     * working directory will be resolved correspondingly to [path]
     * if [path] is null nothing will be done
     * @param path
     */
    fun resolveDirectory(path: Path) {
        workingDirectory = workingDirectory.resolve(path)
    }

    /**
     * resets working directory: sets to initial directory
     */
    fun resetDirectory() {
        workingDirectory = Paths.get(System.getProperty("user.dir"))
    }
}
