package ILDASM

class Converter {
    private val ildasmPath = System.getProperty("user.dir")+"\\ildasm\\ildasm.exe"
    fun convert(inFilePath: String) : CIL.Interpreter{
        val pathToDirectory = inFilePath.substringBeforeLast("\\")+"\\"
        val fileName = inFilePath.substringAfterLast("\\").substringBeforeLast(".")
        val outFilePath = "$pathToDirectory$fileName.txt"
        Runtime.getRuntime().exec("$ildasmPath /text $inFilePath /out=$outFilePath")
        //pause to give ildasm time to create file
        Thread.sleep(2_000)
        return Parser().getMethod(outFilePath)
    }
}