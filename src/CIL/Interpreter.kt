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
     * Pushes a supplied value of type int32 onto the evaluation stack as an int32.
     */
    fun ldc_I4(value: Int){
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