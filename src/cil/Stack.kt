package cil
class Stack(maxStack : Int){
    private val _stack : Array<Any> = Array(maxStack, {0})
    private var _pointer = -1
    private val _maxPointer : Int = maxStack - 1

    fun push(item : Any){
        if (_pointer == _maxPointer) {
            //TODO: remove
            println("STACKOVERFLOW : ${_pointer+1} / ${_maxPointer+1}")
            throw Exception("StackOverflowException")
        }
        _stack[++_pointer] = item
    }

    fun pop() : Any{
        if (_pointer < 0)
            throw Exception("StackOverflowException")
        return _stack[_pointer--]
    }

    fun peek() : Any{
        if (_pointer < 0)
            return Int.MIN_VALUE //fix
        return _stack[_pointer]
    }

    fun isEmpty() : Boolean{
        return(_pointer < 0)
    }
    fun storedNum() : Int{
        return _pointer +1
    }
}