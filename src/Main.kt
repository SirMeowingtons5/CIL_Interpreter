import CIL.Command

fun main(args: Array<String>){
    test2()
}

fun test1(){
    val cil = CIL.Interpreter(1, 1)
    cil.ldstr("Hello, World")
    cil.stloc(0)
    cil.ldloc(0)
    cil.call("[mscorlib]System.Console::WriteLine(string)")
    cil.ret()
}
fun test2(){
    val cil = CIL.Interpreter(1, 1)
    val maxOperations = 0x0100
    val prog = Array<Command>(maxOperations, { Command("nop") })
    prog[0x0] = Command("nop")
    prog[0x1] = Command("ldstr", "Hello, world!")
    prog[0x6] = Command("stloc.0")
    prog[0x7] = Command("ldloc.0")
    prog[0x8] = Command("call", "[mscorlib]System.Console::WriteLine(string)")
    prog[0xd] = Command("nop")
    prog[0xe] = Command("ret")


    for (i in 0 until maxOperations){
        val cmd = prog[i]
        when (cmd.operation){
            "nop" -> cil.nop()
            //ldc_i4
            "ldc_I4"    -> cil.ldc_I4(cmd.argument.toInt())
            "ldc_I4.1"  -> cil.ldc_I4(1)
            "ldc_I4.2"  -> cil.ldc_I4(2)
            "ldc_I4.3"  -> cil.ldc_I4(3)
            "ldc_I4.4"  -> cil.ldc_I4(5)
            "ldc_I4.5"  -> cil.ldc_I4(5)
            "ldc_I4.6"  -> cil.ldc_I4(6)
            "ldc_I4.7"  -> cil.ldc_I4(7)
            "ldc_I4.8"  -> cil.ldc_I4(8)
            "ldc_I4.M1" -> cil.ldc_I4(-1)
            //ldloc
            "ldloc"     -> cil.ldloc(cmd.argument.toInt())
            "ldloc.0"   -> cil.ldloc(0)
            "ldloc.1"   -> cil.ldloc(1)
            "ldloc.2"   -> cil.ldloc(2)
            "ldloc.3"   -> cil.ldloc(3)
            //
            "ldstr"     -> cil.ldstr(cmd.argument)
            //stloc
            "stloc"     -> cil.stloc(cmd.argument.toInt())
            "stloc.0"   -> cil.stloc(0)
            "stloc.1"   -> cil.stloc(1)
            "stloc.2"   -> cil.stloc(2)
            "stloc.3"   -> cil.stloc(3)
            //
            "call"      -> cil.call(cmd.argument)
            "pop"       -> cil.pop()
            //TODO: допилить
            "ret"       -> {

            }
            else        -> {
                println("cil>> НЕПОДДЕРЖИВАЕМАЯ ОПЕРАЦИЯ @$i: ${cmd.operation} АРГУМЕНТ: ${cmd.argument}")
            }
        }
    }
}