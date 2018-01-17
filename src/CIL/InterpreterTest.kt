package CIL

import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.runner.RunWith

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@RunWith(Arquillian::class)
class InterpreterTest {

    @Before
    fun setUp(){
    }

    @Test
    fun add(){
        var actualInt : Int
        var actualDouble : Double
        val cil = Interpreter(2, 0)
        //both are int
        cil.ldc_i4(5)
        cil.ldc_i4(6)
        cil.add()
        actualInt = cil.pop() as Int
        assertEquals(11, actualInt)

        cil.ldc_i4(0)
        cil.ldc_i4(0)
        cil.add()
        actualInt = cil.pop() as Int
        assertEquals(0, actualInt)

        //Int + Double
        cil.ldc_i4(1)
        cil.ldc_r8(2.0)
        cil.add()
        actualDouble = cil.pop() as Double
        assertEquals(3.0, actualDouble)

        //Double + int
        cil.ldc_r8(-3.0)
        cil.ldc_i4(1)
        cil.add()
        actualDouble = cil.pop() as Double
        assertEquals(-2.0, actualDouble)

        //Double + Double
        cil.ldc_r8(3.0)
        cil.ldc_r8(-2.5)
        cil.add()
        actualDouble = cil.pop() as Double
        assertEquals(0.5, actualDouble)
    }

    @Test
    fun sub(){
        var actualInt : Int
        var actualDouble : Double
        val cil = Interpreter(2, 0)

        //both are Int
        cil.ldc_i4(10)
        cil.ldc_i4(5)
        cil.sub()
        actualInt = cil.pop() as Int
        assertEquals(5, actualInt)

        cil.ldc_i4(5)
        cil.ldc_i4(-10)
        cil.sub()
        actualInt = cil.pop() as Int
        assertEquals(-15, actualInt)

        cil.ldc_i4(2)
        cil.ldc_i4(3)
        cil.sub()
        actualInt = cil.pop() as Int
        assertEquals(-1, actualInt)

        // int - double
        cil.ldc_i4(5)
        cil.ldc_r8(3.0)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(2.0, actualDouble)

        cil.ldc_i4(0)
        cil.ldc_r8(-1.5)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(1.5, actualDouble)

        cil.ldc_i4(20)
        cil.ldc_r8(30.0)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(-10.0, actualDouble)

        cil.ldc_i4(0)
        cil.ldc_r8(0.0)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(0.0, actualDouble)

       //double - int
        cil.ldc_r8(2.0)
        cil.ldc_i4(1)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(1.0, actualDouble)

        cil.ldc_r8(1.0)
        cil.ldc_i4(10)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(-9.0, actualDouble)

        cil.ldc_r8(0.0)
        cil.ldc_i4(-5)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(-5.0, actualDouble)

        cil.ldc_r8(0.0)
        cil.ldc_i4(-0)
        cil.sub()
        actualDouble = cil.pop() as Double
        assertEquals(0.0, actualDouble)
    }

    @Test
    fun mul(){
        var actualInt : Int
        var actualDouble : Double
        val cil = Interpreter(2, 0)

        //both are Int
        cil.ldc_i4(2)
        cil.ldc_i4(3)
        cil.mul()
        actualInt = cil.pop() as Int
        assertEquals(6, actualInt)

        cil.ldc_i4(-10)
        cil.ldc_i4(-5)
        cil.mul()
        actualInt = cil.pop() as Int
        assertEquals(50, actualInt)

        cil.ldc_i4(0)
        cil.ldc_i4(0)
        cil.mul()
        actualInt = cil.pop() as Int
        assertEquals(0, actualInt)

        //Int * Double
        cil.ldc_i4(4)
        cil.ldc_r8(2.5)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(10.0, actualDouble)

        cil.ldc_i4(0)
        cil.ldc_r8(7.881)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(0.0, actualDouble)

        cil.ldc_i4(1)
        cil.ldc_r8(0.0)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(0.0, actualDouble)

        cil.ldc_i4(-1)
        cil.ldc_r8(2.5)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(-2.5, actualDouble)

        //Double * Double
        cil.ldc_r8(2.0)
        cil.ldc_r8(5.5)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(11.0, actualDouble)

        cil.ldc_r8(-2.5)
        cil.ldc_r8(-4.0)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(10.0, actualDouble)

        cil.ldc_r8(0.0)
        cil.ldc_r8(0.0)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(0.0, actualDouble)

        cil.ldc_r8(-2.0)
        cil.ldc_r8(0.0)
        cil.mul()
        actualDouble = cil.pop() as Double
        assertEquals(0.0, actualDouble)
    }

    //TODO: реализовать
    //TODO: добавить деление на 0
    @Test
    fun div(){
        var actual : Double
        val cil = Interpreter(2, 0)

        //both are Int
        cil.ldc_i4(6)
        cil.ldc_i4(2)
        cil.div()
        actual = cil.pop() as Double
        assertEquals(3.0, actual)

        cil.ldc_i4(5)
        cil.ldc_i4(2)
        cil.div()
        actual = cil.pop() as Double
        assertEquals(2.0, actual)

        cil.ldc_i4(0)
        cil.ldc_i4(1)
        cil.div()
        actual = cil.pop() as Double
        assertEquals(0.0, actual)

        cil.ldc_i4(1)
        cil.ldc_i4(2)
        cil.div()
        actual = cil.pop() as Double
        assertEquals(0.0, actual)
    }

    companion object {
        @Deployment
        fun createDeployment(): JavaArchive {
            return ShrinkWrap.create(JavaArchive::class.java)
                    .addClass(Interpreter::class.java)
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
        }
    }
}
