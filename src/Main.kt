import CIL.Command

fun main(args: Array<String>){

}

fun test(){
    val maxOperations = 0x100

    val cil = CIL.Interpreter(1,1)
    val prog = Array<Command>(maxOperations, { Command("nop") })
    prog[0x0] = Command("nop")
    prog[0x1] = Command("ldstr", "Hello, world!")
    prog[0x6] = Command("stloc.0")
    prog[0x7] = Command("ldloc.0")
    prog[0x8] = Command("call", "[mscorlib]System.Console::WriteLine(string)")
    prog[0xd] = Command("nop")
    prog[0xe] = Command("ret")

    testSwitch(cil, prog)
}
fun test2(){
    val maxOperations = 0x100

    val cil = CIL.Interpreter(2, 2)
    val prog = Array<Command>(maxOperations, { Command("nop") })
    prog[0x0] = Command("nop")
    prog[0x1] = Command("ldc.i4.0")
    prog[0x2] = Command("stloc.0")
    prog[0x3] = Command("br.s", "IL_0009")
    prog[0x5] = Command("ldloc.0")
    prog[0x6] = Command("ldc.i4.1")
    prog[0x7] = Command("add")
    prog[0x8] = Command("stloc.0")
    prog[0x9] = Command("ldloc.0")
    prog[0xa] = Command("ldc.i4.s", "10")
    prog[0xc] = Command("clt")
    prog[0xe] = Command("stloc.1")
    prog[0xf] = Command("ldloc.1")
    prog[0x10] = Command("brtrue.s", "IL_0005")
    prog[0x12] = Command("ret")

    testSwitch(cil, prog)
}

fun testSwitch(cil : CIL.Interpreter, prog : Array<Command>){
    var pos = 0
    while (pos < prog.size){
        val cmd = prog[pos]
        println("pos : $pos")
        pos++
        when (cmd.operation){
            "nop"       -> cil.nop()
            //transfers
            "beq",
            "beq.s"     ->{
                if (cil.beq())
                    pos = labelToIndex(cmd.argument)
            }
            "bge",
            "bge.s"     ->{
                if (cil.bge())
                    pos = labelToIndex(cmd.argument)
            }
            "bgt",
            "bgt.s"     ->{
                if (cil.bgt())
                    pos = labelToIndex(cmd.argument)

            }
            "ble",
            "ble.s"     ->{
                if (cil.ble())
                    pos = labelToIndex(cmd.argument)

            }
            "blt",
            "blt.s"     ->{
                if (cil.blt())
                    pos = labelToIndex(cmd.argument)
            }
            "br",
             "br.s"     ->{
                pos = labelToIndex(cmd.argument)
            }
            "brtrue",
            "brtrue.s"  ->{
                if (cil.brtrue()){
                    pos = labelToIndex(cmd.argument)
                }
            }
            "brfalse",
            "brfalse.s" ->{
                if (cil.brfalse()){
                    pos = labelToIndex(cmd.argument)
                }
            }
            //
            "add"       -> cil.add()
            "ceq"       -> cil.ceq()
            "cgt"       -> cil.cgt()
            "clt"       -> cil.clt()
            //ldc_i4
            "ldc.i4",
            "ldc.i4.s"  -> cil.ldc_i4(cmd.argument.toInt())
            "ldc.i4.0"  -> cil.ldc_i4(0)
            "ldc.i4.1"  -> cil.ldc_i4(1)
            "ldc.i4.2"  -> cil.ldc_i4(2)
            "ldc.i4.3"  -> cil.ldc_i4(3)
            "ldc.i4.4"  -> cil.ldc_i4(5)
            "ldc.i4.5"  -> cil.ldc_i4(5)
            "ldc.i4.6"  -> cil.ldc_i4(6)
            "ldc.i4.7"  -> cil.ldc_i4(7)
            "ldc.i4.8"  -> cil.ldc_i4(8)
            "ldc.i4.M1" -> cil.ldc_i4(-1)
            //ldloc
            "ldloc",
            "ldloc.s"   -> cil.ldloc(cmd.argument.toInt())
            "ldloc.0"   -> cil.ldloc(0)
            "ldloc.1"   -> cil.ldloc(1)
            "ldloc.2"   -> cil.ldloc(2)
            "ldloc.3"   -> cil.ldloc(3)
            //
            "ldstr"     -> cil.ldstr(cmd.argument)
            //stloc
            "stloc",
            "stloc.s"   -> cil.stloc(cmd.argument.toInt())
            "stloc.0"   -> cil.stloc(0)
            "stloc.1"   -> cil.stloc(1)
            "stloc.2"   -> cil.stloc(2)
            "stloc.3"   -> cil.stloc(3)
            //
            "call"      -> cil.call(cmd.argument)
            "pop"       -> cil.pop()
            //TODO: допилить
            "ret"       -> {
                return
            }
            else        -> {
                println("cil>> НЕПОДДЕРЖИВАЕМАЯ ОПЕРАЦИЯ @$pos: ${cmd.operation} АРГУМЕНТ: ${cmd.argument}")
            }
        }
    }
}

fun labelToIndex(src : String) : Int{
    return src.removePrefix("IL_").toInt(16)
}