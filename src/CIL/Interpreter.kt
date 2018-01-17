package CIL

    val maxOperations = 0x100
class Interpreter(maxStack : Int, maxLocals : Int){
    private val _stack : Stack
    private val _heap = ArrayList<Any>()
    private val _locals : Array<Any>
    private val _commands : Array<Command>

    init {
        _stack = Stack(maxStack)
        _locals = Array(maxLocals, {0})
        _commands = Array(maxOperations, { Command("nop") })
    }

    fun addCommand (index : Int, command : Command){
        _commands[index] = command
    }

    /**
     * Adds two values and pushes the result onto the evaluation stack.
     */
    fun add(){
        var value2 : Any = _stack.pop()
        var value1 : Any = _stack.pop()

        //both are int
        if (value1 is Int && value2 is Int){
            _stack.push(value1 + value2)
            //one is double so result is double
        }else{
            if (value1 is Int){
                value1 = value1.toDouble()
            }
            if (value2 is Int)
                value2 = value2.toDouble()
            _stack.push(value1 as Double + value2 as Double)
        }
    }

    /**
     * Divides two values to return a floating-point result.
     */
    fun div(){
        val value2 : Double
        val value1 : Double
        if (_stack.peek() is Int)
            value2 = (_stack.pop() as Int).toDouble()
        else
            value2 = _stack.pop() as Double
        if (_stack.peek() is Int)
            value1 = (_stack.pop() as Int).toDouble()
        else
            value1 = _stack.pop() as Double
        _stack.push(value1 / value2)
    }

    /**
     * Multiplies two values on the stack.
     */
    fun mul(){
        var value2 : Any = _stack.pop()
        var value1 : Any = _stack.pop()

        //both are int
        if (value1 is Int && value2 is Int){
            _stack.push(value1 * value2)
        //one is double so result is double
        }else{
            if (value1 is Int){
                value1 = value1.toDouble()
            }
            if (value2 is Int)
                value2 = value2.toDouble()
            _stack.push(value1 as Double * value2 as Double)
        }
    }

    fun sub(){
        var value2 : Any = _stack.pop()
        var value1 : Any = _stack.pop()

        //both are int
        if (value1 is Int && value2 is Int){
            _stack.push(value2 - value1)
            //one is double so result is double
        }else{
            if (value1 is Int){
                value1 = value1.toDouble()
            }
            if (value2 is Int)
                value2 = value2.toDouble()
            _stack.push(value2 as Double - value1 as Double)
        }
    }

    /**
     * Compares two values.
     * If they are equal, the integer value 1 is pushed onto the evaluation stack;
     * otherwise 0 is pushed onto the evaluation stack.
     */
    fun ceq(){
        val value2 = _stack.pop()
        val value1 = _stack.pop()
        if (value2.equals(value1))
            _stack.push(1)
        else
            _stack.push(0)

    }

    /**
     * Compares two values.
     * If the first value is greater than the second, the integer value 1 is pushed onto the evaluation stack;
     * otherwise 0 is pushed onto the evaluation stack.
     */
    fun cgt(){
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        if (value1 > value2)
            _stack.push(1)
        else
            _stack.push(0)
    }

    /**
     * Compares two values.
     * If the first value is less than the second, the integer value 1 is pushed onto the evaluation stack;
     * otherwise 0 is pushed onto the evaluation stack.
     */
    fun clt(){
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        if (value1 < value2)
            _stack.push(1)
        else
            _stack.push(0)
    }

    /**
     * Converts the value on top of the evaluation stack to float64.
     */
    fun conv_r8(){
        val value = _stack.pop()
        if (value is Int){
            _stack.push(value.toDouble())
        }else{
            _stack.push(value)
        }
    }


    /**
     * Pushes a supplied value of type int32 onto the evaluation stack as an int32.
     */
    fun ldc_i4(value: Int){
        _stack.push(value)
    }
    fun ldc_r8(value: Double){
        _stack.push(value)
    }

    fun ldstr(value : String){
        _stack.push(Pointer(_heap.size))
        _heap.add(value)
    }
    /**
     * Pops the current value from the top of the evaluation stack
     * and stores it in a the local variable list at a specified index.
     */
    fun stloc(index: Int){
        _locals[index] = _stack.pop()
    }

    /**
     * Loads the local variable at a specific index onto the evaluation stack.
     */
    fun ldloc(index: Int){
        _stack.push(_locals[index])
    }

    /**
     * Calls the method indicated by the passed method descriptor.
     */
    fun call(funcName : String){
        when (funcName){
            "void [mscorlib]System.Console::WriteLine(int32)" ->{
                println((_stack.pop() as Int).toString())
            }
            "void [mscorlib]System.Console::WriteLine(float64)" ->{
                println((_stack.pop() as Double).toString())
            }
            "void [mscorlib]System.Console::WriteLine(string)" ->{
                val index = (_stack.pop() as Pointer).getIndex()
                println(_heap.get(index) as String)
            }
            "string [mscorlib]System.Console::ReadLine()" ->{
                //TODO: чекнуть
                ldstr(readLine() ?: "всё сломалось, зовите фиксиков")
            }
        }
    }

    /**
     * Removes the value currently on top of the evaluation stack.
     */
    fun pop() : Any{
        return _stack.pop()
    }

    /**
     * Returns from the current method, pushing a return value (if present)
     * from the callee's evaluation stack onto the caller's evaluation stack.
     */
    fun ret() : Any?{
        //TODO: реализовать
        if (_stack.isEmpty())
            return null
        else return (_stack.pop())
    }

    /**
     * Fills space if opcodes are patched. No meaningful operation is performed
     */
    fun nop(){

    }

    /*
     *
     * Переходы
     *
     */

    /**
     * Transfers control to a target instruction if two values are equal.
     */
    fun beq() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return value1.equals(value2)
    }

    /**
     * Transfers control to a target instruction if the first value is greater than or equal to the second value.
     */
    fun bge() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 >= value2)
    }

    /**
     * Transfers control to a target instruction if the first value is greater than the second value.
     */
    fun bgt() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 > value2)
    }

    /**
     * Transfers control to a target instruction if the first value is less than or equal to the second value.
     */
    fun ble() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 <= value2)
    }

    /**
     * Transfers control to a target instruction if the first value is less than the second value.
     */
    fun blt() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 < value2)
    }

    /**
     * Transfers control to a target instruction if value is true, not null, or non-zero.
     */
    fun brtrue() : Boolean{
        return (_stack.pop() as Int != 0)
    }

    /**
     * Transfers control to a target instruction if value is false, a null reference, or zero.
     */
    fun brfalse() : Boolean{
        return (_stack.pop() as Int == 0)
    }

    /**
     * Класс-обёртка для указателя
     */
    private class Pointer(index : Int){
        private val _index : Int
        init{
            _index = index
        }
        fun getIndex() : Int{
            return _index
        }
    }


    /**
     * Метод-интерпретатор
     */
    fun run(){
        var pos = 0
        while (pos < maxOperations){
            val cmd = _commands[pos]
            pos++
            when (cmd.operation){
                "nop"       -> nop()
            //transfers
                "beq",
                "beq.s"     ->{
                    if (beq())
                        pos = labelToIndex(cmd.argument)
                }
                "bge",
                "bge.s"     ->{
                    if (bge())
                        pos = labelToIndex(cmd.argument)
                }
                "bgt",
                "bgt.s"     ->{
                    if (bgt())
                        pos = labelToIndex(cmd.argument)

                }
                "ble",
                "ble.s"     ->{
                    if (ble())
                        pos = labelToIndex(cmd.argument)

                }
                "blt",
                "blt.s"     ->{
                    if (blt())
                        pos = labelToIndex(cmd.argument)
                }
                "br",
                "br.s"     ->{
                    pos = labelToIndex(cmd.argument)
                }
                "brtrue",
                "brtrue.s"  ->{
                    if (brtrue()){
                        pos = labelToIndex(cmd.argument)
                    }
                }
                "brfalse",
                "brfalse.s" ->{
                    if (brfalse()){
                        pos = labelToIndex(cmd.argument)
                    }
                }
            //
                "add"       -> add()
                "div"       -> div()
                "mul"       -> mul()
                "sub"       -> sub()
            //
                "ceq"       -> ceq()
                "cgt"       -> cgt()
                "clt"       -> clt()
            //
                "conv.r8"   -> conv_r8()
            //ldc_i4
                "ldc.i4",
                "ldc.i4.s"  -> ldc_i4(cmd.argument.toInt())
                "ldc.i4.0"  -> ldc_i4(0)
                "ldc.i4.1"  -> ldc_i4(1)
                "ldc.i4.2"  -> ldc_i4(2)
                "ldc.i4.3"  -> ldc_i4(3)
                "ldc.i4.4"  -> ldc_i4(5)
                "ldc.i4.5"  -> ldc_i4(5)
                "ldc.i4.6"  -> ldc_i4(6)
                "ldc.i4.7"  -> ldc_i4(7)
                "ldc.i4.8"  -> ldc_i4(8)
                "ldc.i4.M1" -> ldc_i4(-1)
                "ldc.r8"    -> ldc_r8(cmd.argument.toDouble())
            //ldloc
                "ldloc",
                "ldloc.s"   -> ldloc(cmd.argument.toInt())
                "ldloc.0"   -> ldloc(0)
                "ldloc.1"   -> ldloc(1)
                "ldloc.2"   -> ldloc(2)
                "ldloc.3"   -> ldloc(3)
            //
                "ldstr"     -> ldstr(cmd.argument)
            //stloc
                "stloc",
                "stloc.s"   -> stloc(cmd.argument.toInt())
                "stloc.0"   -> stloc(0)
                "stloc.1"   -> stloc(1)
                "stloc.2"   -> stloc(2)
                "stloc.3"   -> stloc(3)
            //
                "call"      -> call(cmd.argument)
                "pop"       -> pop()
            //TODO: допилить
                "ret"       -> {
                    return
                }
                else        -> {
                    println("cil>> UNSUPPORTED OPERATION @$pos: ${cmd.operation} ARGUMENT: ${cmd.argument}")
                }
            }
        }
    }
    private fun labelToIndex(src : String) : Int{
        return src.removePrefix("IL_").toInt(16)
    }
}