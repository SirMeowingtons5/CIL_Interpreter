package cil

object Instance{
    private var entryMethod : Method = Method(0, 0, "DULL", Class("DULL"))
    private val _classes = HashMap<String, Class>()

    fun setEntryMethod(value : Method){
        entryMethod = value
    }
    fun addClass(name : String, value : Class){
        _classes.put(name, value)
    }
    fun run(){
        entryMethod.run()
    }
}