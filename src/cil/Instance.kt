package cil

object Instance{
    private var entryMethod : Method = Method(0, 0, "DULL", Class("DULL"))
    private val _classes = HashMap<String, Class>()
    val heap = ArrayList<Any>()
    val nullObject = Pointer(-1)

    fun setEntryMethod(value : Method){
        entryMethod = value
    }
    fun addClass(name : String, value : Class){
        _classes.put(name, value)
    }
    fun getClass(name : String) : Class{
        return _classes[name]!!
    }
    fun run(){
        //starting args are not supported (yet)
        val emptyList = ArrayList<Any>()
        entryMethod.run(ArrayList<Any>())
    }
    fun createClass(name : String) : Class{
        return _classes[name]!!.create()
    }
}