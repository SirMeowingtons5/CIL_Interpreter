package CIL

class Interpreter(maxStack : Int, maxLocals : Int){
    private val _stack : Stack
    private val _heap = ArrayList<Any>()
    private val _locals : Array<Any>

    init {
        _stack = Stack(maxStack)
        _locals = Array(maxLocals, {0})
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
            "[mscorlib]System.Console::WriteLine(int32)" ->{
                println((_stack.pop() as Int).toString())
            }
            "[mscorlib]System.Console::WriteLine(float64)" ->{
                println((_stack.pop() as Double).toString())
            }
            "[mscorlib]System.Console::WriteLine(string)" ->{
                val index = (_stack.pop() as Pointer).getIndex()
                println(_heap.get(index) as String)
            }
            "[mscorlib]System.Console::ReadLine()" ->{
                //TODO: чекнуть
                ldstr(readLine() ?: "всё сломалось, зовите фиксиков")
            }
        }
    }

    /**
     * Removes the value currently on top of the evaluation stack.
     */
    fun pop(){
        _stack.pop()
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
}