import cil.Instance
import ildasm.Parser

fun main(args: Array<String>){
    println("Enter filename without extension (i.e. test instead of test.exe)")
    val fileName = readLine() ?: "HelloWorld"
    val parser = Parser()
    parser.parse(fileName)

    println("cil>>STARTED")
    Instance.run()
    println("cil>>FINISHED")
}