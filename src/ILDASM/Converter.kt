package ILDASM

import CIL.Command
import java.io.FileInputStream


class Converter {
    /**
     * group 1 - full method title with name and attributes
     * group 4 - method name
     * group 5 - method body, needs trimming to curve closing bracket, watch getMethod implementation
     */
    //private val methodRegex = Regex("\\.method (([a-zA-Z])+ )+ ([a-zA-Z]+)")
    private val methodRegex = Regex("\\.method (([a-zA-Z])+ )+ (([a-zA-Z]+).*)\\s*((.*\r\n)*)",
            RegexOption.MULTILINE)
    /**
     * group 1 - Stack size number
     */
    private val maxstackRegex = Regex("\\.maxstack\\s*(\\d*)")
    /**
     * group 0 - full line
     * group 1 - IL index in hex
     * group 3 - command
     * group 5 - argument (size 1 if empty)
     */
    private val commandRegex = Regex("IL_((\\d|[a-f])*):\\s*(([a-z]|\\d|\\.)*)(\\s*(.*))?")

    private val localsRegex = Regex("V_(\\d*)")


    private fun readFromFile(fileName : String) : String{
        val path = System.getProperty("user.dir")
        val input = FileInputStream("$path\\ildasm\\$fileName")
        val inputAsString = input.bufferedReader().use { it.readText() }
        return inputAsString
    }

    fun getMethod(fileName: String) : CIL.Interpreter{
        val programText = readFromFile(fileName)
        val methodName = methodRegex.find(programText)?.groupValues?.get(4) ?: "nullName"
        val methodBody = methodRegex.find(programText)?.groupValues?.get(5)?.
                substringAfter("{")?.substringBefore("}") ?: "nullBody"
        val maxStack = maxstackRegex.find(methodBody)?.groupValues?.get(1)?.toInt() ?: 0
        val localsBody = methodBody.substringAfter(".locals init (").substringBefore(")")
        val localsSequence = localsRegex.findAll(localsBody)
        val maxLocals : Int
        if (localsSequence.count() > 0) {
            maxLocals = localsSequence.last().groupValues.get(1).toInt() + 1
        }else{
            maxLocals = 0
        }
        val cil = CIL.Interpreter(maxStack, maxLocals)

        val commands = methodBody.split("\n")
        commands.forEach { commandRegex.find(it)?.groupValues?.let{
            if(it.get(5).length > 1) {
                cil.addCommand(it.get(1).toInt(16), Command(it.get(3), it.get(6)))
            }else{
                cil.addCommand(it.get(1).toInt(16), Command(it.get(3)))
            }
        } }

        return cil
    }

}