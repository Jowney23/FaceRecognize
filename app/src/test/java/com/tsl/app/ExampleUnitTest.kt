package com.tsl.app

import com.jowney.common.util.tsl.Desensitize4ewm
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun en_de_crypt() {
        println("----开始---")
        var encryptData:  String = Desensitize4ewm.encrypt("测试测试测试\\", "123")
        println(encryptData)
        var decryptData:  String =Desensitize4ewm.decrypt(encryptData,"123")
        println(decryptData)
        println("----结束---")
    }

    @Test
    fun aalist(){
        val content = mutableListOf<String>()
        content.add("wwwwww")

        println(content)
    }
}
