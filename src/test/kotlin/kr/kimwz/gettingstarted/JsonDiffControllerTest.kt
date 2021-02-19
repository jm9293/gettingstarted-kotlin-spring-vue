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

        val json1str = "{\"Aidan Gillen\": {\"array\": [\"Game of Thron\\\"es\",\"The Wire\"],\"string\": \"some string\",\"int\": 2,\"aboolean\": true, \"boolean\": true,\"object\": {\"foo\": \"bar\",\"object1\": {\"new prop1\": \"new prop value\"},\"object2\": {\"new prop1\": \"new prop value\"},\"object3\": {\"new prop1\": \"new prop value\"},\"object4\": {\"new prop1\": \"new prop value\"}}},\"Amy Ryan\": {\"one\": \"In Treatment\",\"two\": \"The Wire\"},\"Annie Fitzgerald\": [\"Big Love\",\"True Blood\"],\"Anwan Glover\": [\"Treme\",\"The Wire\"],\"Alexander Skarsgard\": [\"Generation Kill\",\"True Blood\"], \"Clarke Peters\": null}" // 다른값일시 기준으로 result에 입력되는 json
        val json2str = "{\"Aidan Gillen\": {\"array\": [\"Game of Thrones\",\"The Wire\"],\"string\": \"some string\",\"int\": \"2\",\"otherint\": 4, \"aboolean\": \"true\", \"boolean\": false,\"object\": {\"foo\": \"bar\"}},\"Amy Ryan\": [\"In Treatment\",\"The Wire\"],\"Annie Fitzgerald\": [\"True Blood\",\"Big Love\",\"The Sopranos\",\"Oz\"],\"Anwan Glover\": [\"Treme\",\"The Wire\"],\"Alexander Skarsg?rd\": [\"Generation Kill\",\"True Blood\"],\"Alice Farmer\": [\"The Corner\",\"Oz\",\"The Wire\"]}"

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