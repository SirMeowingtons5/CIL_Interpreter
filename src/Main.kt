import cil.Instance
import ildasm.Parser

fun main(args: Array<String>){

    val path = System.getProperty("user.dir")+"\\test\\classes2.txt"
    val parser = Parser()
    parser.parse(path)

    println("cil>>STARTING")
    Instance.run()
    println("cil>>FINISHED")

}