package cil

import ildasm.Parser


class Method(private val maxStack : Int, private val maxLocals : Int, val name : String,
             private var classInstance: Class){
    //null dummy object
    private val nullObject = Pointer(-1)
    private val _maxOperations = 0x100
    private val _stack : Stack = Stack(maxStack)
    private val _locals : Array<Any> = Array(maxLocals, {nullObject})
    private val _commands : Array<Command>

    init {
        _commands = Array(_maxOperations, { Command("nop", "") })
    }

    /**
     * Returns new instance of Method
     * Further realisation for static methods
     */
    fun setClassInstance(instance : Class){
        classInstance = instance
    }
    fun create() : Method{
        val res = Method(maxStack, maxLocals, name, classInstance)
        res.setCommands(_commands)
        return res
    }

    fun addCommand (index : Int, command : Command){
        _commands[index] = command
    }
    private fun setCommands(commands : Array<Command>){
        for (i in 0 until _maxOperations)
            _commands[i] = commands[i]
    }

    /**
     * Adds two values and pushes the result onto the evaluation stack.
     */
    private fun add(){
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
    private fun div(){
        val value2 : Double = if (_stack.peek() is Int)
            (_stack.pop() as Int).toDouble()
        else
            _stack.pop() as Double
        val value1 : Double = if (_stack.peek() is Int)
            (_stack.pop() as Int).toDouble()
        else
            _stack.pop() as Double
        _stack.push(value1 / value2)
    }

    /**
     * Multiplies two values on the stack.
     */
    private fun mul(){
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

    private fun sub(){
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
    private fun ceq(){
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
    private fun cgt(){
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
    private fun clt(){
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
    private fun conv_r8(){
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
    private fun ldc_i4(value: Int){
        _stack.push(value)
    }
    private fun ldc_r8(value: Double){
        _stack.push(value)
    }

    private fun ldstr(value : String){
        val parsedValue = value.removePrefix("\"").removeSuffix("\"")
        if (Instance.heap.contains(parsedValue)){
            _stack.push(Pointer(Instance.heap.indexOf(parsedValue)))
        }else {
            _stack.push(Pointer(Instance.heap.size))
            Instance.heap.add(parsedValue)
        }
    }
    private fun ldarg_0(){
        _stack.push(classInstance.getSelfPointer())
    }

    private fun stfld(name : String){
        val fieldName = name.substringAfterLast(" ")
        classInstance.stfld(fieldName, _stack.pop())
    }
    private fun ldfld(name : String){
        val fieldName = name.substringAfterLast(" ")
        _stack.push(classInstance.ldfld(fieldName))
    }

    private fun newobj(arg : String){
        val className = arg.substringBefore("::").substringAfterLast(" ")
        val methodName = arg.substringAfterLast(" ")
        val argsNumber : Int
        if (methodName.substringAfter("(").substringBefore(")").length == 0) {
            argsNumber = 0
        }else{
            //get number of args by couting commas
            argsNumber = methodName
                    .substringBefore("(").substringAfter(")").split(",").size

        }
        val args = ArrayList<Any>()
        for (i in 1..argsNumber){
            args.add(_stack.pop())
        }
        _stack.push(Instance.getClass(className).create().call(methodName, args) as Any)
    }
    /**
     * Pops the current value from the top of the evaluation stack
     * and stores it in a the local variable list at a specified index.
     */
    private fun stloc(index: Int){
        _locals[index] = _stack.pop()
    }

    /**
     * Loads the local variable at a specific index onto the evaluation stack.
     */
    private fun ldloc(index: Int){
        _stack.push(_locals[index])
    }

    //TODO: add
    private fun ldnull(){
        _stack.push(nullObject)
    }
    //TODO: add
    private fun dup(){
        _stack.push(_stack.peek())
    }

    /**
     * Calls the method indicated by the passed method descriptor.
     */
    private fun call(funcName : String){
        when (funcName){
            "int32 [mscorlib]System.Int32::Parse(string)"   ->{
                val str = Instance.heap[(_stack.pop() as Pointer).getIndex()] as String
                _stack.push(str.toInt())
            }
            "void [mscorlib]System.Console::WriteLine(int32)" ->{
                println((_stack.pop() as Int).toString())
            }
            "void [mscorlib]System.Console::WriteLine(float64)" ->{
                println((_stack.pop() as Double).toString())
            }
            "void [mscorlib]System.Console::WriteLine(string)" ->{
                val index = (_stack.pop() as Pointer).getIndex()
                println("cil>>WRITELINE:${Instance.heap.get(index) as String}")
            }
            "string [mscorlib]System.Console::ReadLine()" ->{
                print("cil>>READLINE:")
                ldstr(readLine() ?: "всё сломалось, зовите фиксиков")
            }
            "string [mscorlib]System.String::Concat(string, string)" ->{
                val value2 = _stack.pop() as Pointer
                val value1 = _stack.pop() as Pointer
                val res = Instance.heap.get(value1.getIndex()) as String +
                        Instance.heap.get(value2.getIndex()) as String
                ldstr(res)
            }
            "instance void [mscorlib]System.Object::.ctor()" ->{
                //do nothing
            }
            else ->{
                //same implementation
                callvirt(funcName)
            }

        }
    }
    //fix for multiple args
    private fun callvirt(arg : String){
        val p = Parser()
        val methodName = p.getCallMethodName(arg)
        val argsNumber = p.getCallMethodArgsNum(arg)
        /*val methodName = arg.substringAfterLast(" ")
        val argsNumber : Int
        if (methodName.substringAfter("(").substringBefore(")").length == 0) {
            argsNumber = 0
        }else{
            //get number of args by couting commas, +1 cause 0 commas means 1 arg, 1 comma means 2 args etc.
            argsNumber = methodName
                    .substringBefore("(").substringAfter(")").split(",").size

        }*/
        val args = ArrayList<Any>()
        for (i in 1..argsNumber){
            args.add(_stack.pop())
        }
        val pointer = _stack.pop() as Pointer
        val cls = Instance.heap[pointer.getIndex()] as Class

        val res = cls.callvirt(methodName, args)
        if (res != null && !arg.substringBeforeLast(" ").contains("void")){
            _stack.push(res)
        }
    }

    /**
     * Removes the value currently on top of the evaluation stack.
     */
    private fun pop() : Any{
        return _stack.pop()
    }

    /**
     * Returns from the current method, pushing a return value (if present)
     * from the callee's evaluation stack onto the caller's evaluation stack.
     */
    private fun ret() : Any?{
        if (!_stack.isEmpty()){
            return _stack.pop()
        }else{
            return null
        }
    }

    /**
     * Fills space if opcodes are patched. No meaningful operation is performed
     */
    private fun nop(){

    }

    /*
     *
     * Jumps
     *
     */

    /**
     * Transfers control to a target instruction if two values are equal.
     */
    private fun beq() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return value1.equals(value2)
    }

    /**
     * Transfers control to a target instruction if the first value is greater than or equal to the second value.
     */
    private fun bge() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 >= value2)
    }

    /**
     * Transfers control to a target instruction if the first value is greater than the second value.
     */
    private fun bgt() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 > value2)
    }

    /**
     * Transfers control to a target instruction if the first value is less than or equal to the second value.
     */
    private fun ble() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 <= value2)
    }

    /**
     * Transfers control to a target instruction if the first value is less than the second value.
     */
    private fun blt() : Boolean{
        val value2 = _stack.pop() as Int
        val value1 = _stack.pop() as Int
        return (value1 < value2)
    }

    /**
     * Transfers control to a target instruction if value is true, not null, or non-zero.
     */
    private fun brtrue() : Boolean{
        return (_stack.pop() as Int != 0)
    }

    /**
     * Transfers control to a target instruction if value is false, a null reference, or zero.
     */
    private fun brfalse() : Boolean{
        return (_stack.pop() as Int == 0)
    }



    /**
     * Interpreter method
     */
    fun run(args : ArrayList<Any>) : Any?{
        var pos = 0
        while (pos < _maxOperations){
            val cmd = _commands[pos]
            pos++
            //TODO: remove
            if(cmd.operation != "nop") {
                //println("${pos.toString(16)}) ${cmd.operation} ${cmd.argument} [${_stack.storedNum()}/$maxStack]")
            }
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
                //TODO: add
                "cgt.un"    -> cgt()
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
            //newobj
                "newobj"    -> newobj(cmd.argument)
            //ldloc
                "ldloc",
                "ldloc.s"   -> ldloc(cmd.argument.toInt())
                "ldloc.0"   -> ldloc(0)
                "ldloc.1"   -> ldloc(1)
                "ldloc.2"   -> ldloc(2)
                "ldloc.3"   -> ldloc(3)
            //
                "ldstr"     -> ldstr(cmd.argument)
            //ldarg0 == "this" keyword
                "ldarg.0"   -> ldarg_0()
                "ldarg.1"   -> _stack.push(args[0])
                "ldarg.2"   -> _stack.push(args[1])
                "ldarg.3"   -> _stack.push(args[2])
                "ldarg"     -> _stack.push(args[cmd.argument.toInt()-1])
                "ldarga",
                "ldargs"    -> _stack.push(args[cmd.argument.toInt()-1])
            //stloc
                "stloc",
                "stloc.s"   -> stloc(cmd.argument.toInt())
                "stloc.0"   -> stloc(0)
                "stloc.1"   -> stloc(1)
                "stloc.2"   -> stloc(2)
                "stloc.3"   -> stloc(3)
            //fld
                "stfld"     -> stfld(cmd.argument)
                "ldfld"     -> ldfld(cmd.argument)
            //
                "call"      -> call(cmd.argument)
                "callvirt"  -> callvirt(cmd.argument)
                "pop"       -> pop()
            //
                "ret"       -> return ret()
            //new
                "dup"       -> dup()
                "ldnull"    -> ldnull()
                else        -> {
                    println("cil>> UNSUPPORTED OPERATION @$pos: ${cmd.operation} ARGUMENT: ${cmd.argument}")
                }
            }
        }
        return null //unreachable
    }
    private fun labelToIndex(src : String) : Int{
        return src.removePrefix("IL_").toInt(16)
    }
}