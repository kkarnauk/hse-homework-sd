package ru.hse.cli.executor

import ru.hse.cli.executor.commands.*

/**
 * Represents static storage that stores the map from a command name to an instance of the class.
 */
object CommandStorage {
    private var storage: HashMap<String, AbstractCommand> = hashMapOf(
        "wc" to WcCommand(),
        "echo" to EchoCommand(),
        "exit" to ExitCommand(),
        "cat" to CatCommand(),
        "pwd" to PwdCommand(),
        "assignment" to AssignmentCommand(),
        "grep" to GrepCommand(),
        "ls" to LsCommand(),
        "cd" to CdCommand()
    )

    /**
     * Get the instance of the command class by a command name.
     * @param commandName
     * @return the instance of the class.
     * @exception CommandIsNotFoundException will be raised if the given command doesn't exist in the storage.
     */
    fun getCommand(commandName: String): AbstractCommand {
        return storage.getOrDefault(commandName, ExternalCommand())
    }
}
