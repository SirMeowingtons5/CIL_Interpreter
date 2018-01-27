package cil

class Class(val name : String){
    private val _methods = HashMap<String, Method>()
    private val _fields = HashMap<String, Any>()

    fun addMethod(method : Method){
        _methods.put(method.name, method)
    }
    private fun setMethods(methods : HashMap<String, Method>){
        _methods.clear()
        _methods.putAll(methods)
    }
    fun getMethod(name : String) : Method{
        return _methods[name]!!.create()
    }
    fun addField(name : String){
        _fields.put(name, 0)
    }
    private fun setFields(fields : HashMap<String, Any>){
        _fields.clear()
        _fields.putAll(fields)
    }

    fun stfld(name : String, value : Any){
        _fields.replace(name, value)
    }
    fun ldfld(name : String) : Any{
        return _fields[name]!!
    }
    fun callvirt(){
        //TODO : implement
    }

    fun create() : Class{
        val res = Class(name)
        res.setMethods(_methods)
        res.setFields(_fields)
        return res
    }

}