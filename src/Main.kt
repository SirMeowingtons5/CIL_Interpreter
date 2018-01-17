import ILDASM.Converter
import ILDASM.Parser


fun main(args: Array<String>){
    println("Input .exe file location")
    val filePath = readLine()
    val c = Converter()
    val cil = c.convert(filePath ?: "D:\\IdeaProjects\\CIL_Interpreter\\ildasm\\app.exe")
    println("cil>>STARTING")
    cil.run()
    println("cil>>FINISHED")
}

