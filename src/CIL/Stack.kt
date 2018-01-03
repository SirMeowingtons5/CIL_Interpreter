package CIL
class Stack(maxStack : Int){
    private val _stack : Array<Any>
    private var _pointer = -1
    private val _maxPointer : Int
    init {
        _stack = Array<Any>(maxStack, {0})
        _maxPointer = maxStack - 1
    }

    fun push(item : Any){
        //TODO: реализовать эксепшн
        if (_pointer == _maxPointer)
            throw Exception("StackOverflowException")
        _stack[++_pointer] = item
    }

    fun pop() : Any{
        if (_pointer < 0)
            throw Exception("StackOverflowException")
        return _stack[_pointer--]
    }

    fun peek() : Any{
        if (_pointer < 0)
            throw Exception("StackOverflowException")
        return _stack[_pointer]
    }

    fun isEmpty() : Boolean{
        return(_pointer < 0)
    }
}