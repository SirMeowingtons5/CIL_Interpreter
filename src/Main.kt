import ILDASM.Converter


fun main(args: Array<String>){
    val c = Converter()
    c.getMethod("app2.txt").run()
    println("cil>> PROCESS FINISHED")
}

