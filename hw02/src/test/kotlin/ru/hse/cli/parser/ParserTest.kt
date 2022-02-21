package ru.hse.cli.parser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.hse.cli.parser.impl.ParserImpl
import ru.hse.cli.parser.util.Command

class ParserTest {

    @Test
    fun testOneArg() = doTest("cat test.txt", listOf(Command("cat", listOf("test.txt"))))

    @Test
    fun testDoubleQuotes() = doTest("echo \"Hello, 'ee'world!\"", listOf(Command("echo", listOf("Hello, 'ee'world!"))))

    @Test
    fun testSingleQuotes() = doTest("echo 'nan\"\"ana'", listOf(Command("echo", listOf("nan\"\"ana"))))

    @Test
    fun testCommandWithoutArgs() = doTest("pwd", listOf(Command("pwd", emptyList())))

    @Test
    fun testSeveralArgs() = doTest("wc a.txt b.txt", listOf(Command("wc", listOf("a.txt", "b.txt"))))

    @Test
    fun testCommandInQuotes() = doTest("\"echo\" 'lol'", listOf(Command("echo", listOf("lol"))))

    @Test
    fun testSpaces() = doTest("echo      'fewe    f '   ", listOf(Command("echo", listOf("fewe    f "))))

    @Test
    fun testAssignment() = doTest("a = bbb ", listOf(Command("assignment", listOf("a", "bbb"))))

    @Test
    fun testAssignmentWithoutSpaces() = doTest("a='b'", listOf(Command("assignment", listOf("a", "b"))))

    @Test
    fun testPipe() = doTest("echo abc | wc | echo", listOf(Command("echo", listOf("abc")), Command("wc"), Command("echo")))

    @Test
    fun testPipeWithoutSpaces() = doTest("echo a|cat", listOf(Command("echo", listOf("a")), Command("cat")))

    @Test
    fun testPipeArguments() = doTest("echo b | wc a.txt b.txt | cat",
        listOf(Command("echo", listOf("b")), Command("wc", listOf("a.txt", "b.txt")), Command("cat")))

    @Test
    fun testPipeAndAssignment() = doTest("echo a | b = a | wc", listOf(Command("echo", listOf("a")),
        Command("assignment", listOf("b", "a")), Command("wc")))

    @Test
    fun testLs() = doTest("ls", listOf(Command("ls", emptyList())))

    @Test
    fun testLsSingleArgument() = doTest("ls name", listOf(Command("ls", listOf("name"))))

    private fun doTest(input: String, expectedCommands: List<Command>) {
        val parser = ParserImpl(input)

        val commands = parser.parse()

        Assertions.assertEquals(expectedCommands, commands)
    }
}
