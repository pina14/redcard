package appdev.pina.redcard

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test_async() {
        var res = false
        runBlocking {
            val a1 = async {
                delay(1000)
                println("a1 async")

                async {
                    delay(2000)
                    println("a2 async")
                }
                async {
                    delay(2000)
                    println("a3 async")
                    res = true

                }
            }

            a1.await()

            assertTrue(res)
        }

        Thread.sleep(4000)
    }
}
