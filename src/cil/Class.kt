package cil

class Class(val name : String){
    private val _methods = HashMap<String, Method>()
    private val _fields = HashMap<String, Any>()
    private var _selfPointer : Pointer = Pointer(0) //temp

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
        if(!_fields.containsKey(name)) {
            _fields.put(name, 0)
        }
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
    fun call(methodName: String, args: ArrayList<Any>) : Any?{
        return getMethod(methodName).run(args)
    }
    fun callvirt(methodName: String, args : ArrayList<Any>) : Any?{
        return call(methodName, args)
    }

    fun extend(name : String){
        val parent = Instance.getClass(name)
        _methods.putAll(parent._methods)
        _fields.putAll(parent._fields)
    }

    fun getSelfPointer() : Pointer{
        return _selfPointer
    }

    fun create() : Class{
        val res = Class(name)
        res.setMethods(_methods)
        res.setFields(_fields)
        res._methods.forEach { t, u -> u.setClassInstance(res) }
        res._selfPointer = Pointer(Instance.heap.size)
        Instance.heap.add(res)
        return res
    }

}