package ildasm

import cil.Command
import cil.Instance
import cil.Method
import java.io.File
import java.io.FileInputStream


class Parser{
    private val ildasmDirectory = System.getProperty("user.dir")+"\\ildasm\\"
    private val ildasmPath = ildasmDirectory+"ildasm.exe"
    /**
     * group 1 - access modifier (public or private)
     * group 2 - class name
     * group 3 - parent class
     * group 4 - body without curved braces
     */
    private val classRegex = Regex(
            "\\.class (private|public) auto ansi beforefieldinit (.*)\r\n\\s*extends" +
                    "\\s(.*)\r\n\\{([\\s\\S]*?(?=^}))}",
            RegexOption.MULTILINE)

    /**
     * group 0 - full method
     * group 1 - method title (modifiers, name, args)
     * group 2 - access modifier
     * group 4 - name
     * group 5 - arguments
     * group 6 - body
     */
    private val methodRegex = Regex(
            "(\\.method (public|private) (\\w*\\s[\r\n]*)*([.a-zA-Z0-9]*)" +
                    "\\(([\\s\\S]*?)\\) cil managed\\s*)\\{([\\s\\S]+?(?=\\s*}))}",
            RegexOption.MULTILINE)
    /**
     * group 0 - entire command
     * group 1 - command index in hex
     * group 2 - command name
     * group 3 - (optional) command argument (needs whitespace trimming at start)
     * group 4 - (optional) multiline command argument (need whitespaces trimming, see realisation)
     */
    private val commandRegex = Regex("IL_([\\da-f]*):\\s+([\\w.]*)\\s*?((,\r\n|[^\r\n])*)")

    private val fieldRegex = Regex("\\.field [a-zA-Z ]* (\\w*)")
    /**
     * group 1 - Stack size number
     */
    private val maxstackRegex = Regex("\\.maxstack\\s*(\\d*)")

    private val localsRegex = Regex("V_(\\d*)")
    private val entrypointRegex = Regex("^\\s*.entrypoint")


    /**
     * Regex for parcing call / callvirt args
     * group 0 - full method name with args
     * group 2 - args only
     */
    private val callRegex = Regex("([^\\s]*)::[.|\\w]*\\((.*)\\)")


    private fun readFile(fileName: String) : String{
        val filePath = "$ildasmDirectory$fileName.txt"
        val input = FileInputStream(filePath)
        val inputAsString = input.bufferedReader().use { it.readText() }
        return inputAsString
    }
    private fun convert(fileName: String){
        val inFilePath = "$ildasmDirectory$fileName.exe"
        val outFilePath = "$ildasmDirectory$fileName.txt"
        val file = File(outFilePath)
        if (file.exists()){
            file.delete()
        }
        Runtime.getRuntime().exec("$ildasmPath /text $inFilePath /out=$outFilePath")
        do{
            //pause to give ildasm time to create file
            Thread.sleep(500)
        }while(!file.exists())
    }

    fun parse(fileName: String){
        convert(fileName)
        val listing = readFile(fileName)
        classRegex.findAll(listing).forEach {
            val cls = cil.Class(it.groupValues[2])
            //adding fields
            fieldRegex.findAll(it.groupValues[4]).forEach {
                cls.addField("${cls.name}::${it.groupValues[1]}")
            }
            //adding methods
            methodRegex.findAll(it.groupValues[4]).forEach {
                val method : Method
                val args = it.groupValues[5]
                        .replace(Regex("\\s+"), " ")
                        .replace(Regex("\\w+$"), "")
                        .replace(Regex("\\s+\\w+,"), ",")
                        .trim()
                val methodName = "${cls.name}::${it.groupValues[4]}($args)"
                val maxStack = maxstackRegex.find(it.groupValues[6])?.groupValues?.get(1)?.toInt() ?: 0
                val localsSequence = localsRegex.findAll(it.groupValues[6])
                val maxLocals = if (localsSequence.count() > 0){
                    localsSequence.last().groupValues[1].toInt()+1
                }else{
                    0
                }
                method = Method(maxStack, maxLocals, methodName, cls)
                commandRegex.findAll(it.groupValues[6]).forEach {
                    val command : Command
                    val index = it.groupValues[1].toInt(16)
                    val operation = it.groupValues[2]
                    val argument : String = it.groupValues[3]
                            .replace(Regex("\\s+"), " ")
                            .trim()
                    command = Command(operation, argument)
                    method.addCommand(index, command)
                }
                if (it.groupValues[6].contains(entrypointRegex)){
                    Instance.setEntryMethod(method)
                }
                cls.addMethod(method)
            }
            if(it.groupValues[3] != "[mscorlib]System.Object") {
                cls.extend(it.groupValues[3])
            }
            Instance.addClass(cls.name, cls)
        }
    }

    fun getCallMethodName(source : String) : String{
        val res = callRegex.find(source)?.groupValues?.get(0) ?: "NULL"
        return res
    }
    fun getCallMethodArgsNum(source: String) : Int{
        val str = callRegex.find(source)?.groupValues?.get(2) ?: ""
        if (str.length == 0){
            return 0
        }else{
            return str.split(",").size
        }
    }
}