package cil

class Pointer(index : Int){
    private val _index : Int
    init{
        _index = index
    }
    fun getIndex() : Int{
        return _index
    }
}