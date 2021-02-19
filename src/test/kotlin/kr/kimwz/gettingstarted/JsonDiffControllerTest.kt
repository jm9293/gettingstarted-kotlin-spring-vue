package kr.kimwz.gettingstarted


import kr.kimwz.gettingstarted.repository.JsonDiffRepository
import kr.kimwz.gettingstarted.service.JsonDiffService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest

class JsonDiffControllerTest() {

    @Autowired
    private val jsonDiffRepository : JsonDiffRepository? = null

    @Autowired
    private val jsonDiffService: JsonDiffService? = null


    @Test
    fun test() {

        val json1str = "{\n" +
                "   \"users\": [{\"skills\": [{\"skillname\":\"JAVA\", \"level\":\"row\"}]}]\n" +
                "}" // 다른값일시 기준으로 result에 입력되는 json
        val json2str = "{\n" +
                "   \"users\": [{\"skills\": [{\"skillname\":\"JAVA\", \"level\":\"row\"},\"2\"]}]\n" +
                "}"

        //test 1 같은것
        jsonDiffService?.compareJson(json1str,json1str,"test1")
        val res = jsonDiffService?.findDiffJson("test1")
        
        assert(res?.bool == "true");

        //test 2 다른것
        jsonDiffService?.compareJson(json1str,json2str,"test2")
        val res2 = jsonDiffService?.findDiffJson("test2")

        assert(res2?.bool == "false");

        jsonDiffRepository?.deleteAll();

    }




}