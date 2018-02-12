import cil.Instance
import ildasm.Parser
import java.lang.ClassCastException

fun main(args: Array<String>){
    println("Enter filename without extension (i.e. test instead of test.exe)")
    //val fileName = readLine() ?: "HelloWorld"
    val fileName = "final5"
    val parser = Parser()
    parser.parse(fileName)

    println("cil>>STARTED")
    val heap = Instance.heap //debug
    try {
        Instance.run()
    }catch (e : ClassCastException){

    }
    println("cil>>FINISHED")
}