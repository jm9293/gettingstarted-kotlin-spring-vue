package kr.kimwz.gettingstarted

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.collections.HashMap

class JsonDiffControllerTest {

    @Test
    fun test() {
        val json1str = "{\n" +
                "   \"users\": [{\"skills\": [{\"skillname\":\"JAVA\", \"level\":\"high\"}]}]\n" +
                "}" // 다른값일시 기준으로 result에 입력되는 json
        val json2str = "{\n" +
                "   \"users\": [{\"skills\": [{\"skillname\":\"JAVA\", \"level\":\"row\"},\"2\"]}]\n" +
                "}"

        val mapper = jacksonObjectMapper()
        val json1 = mapper.readValue<MutableMap<String, Any>>(json1str)
        val json2 = mapper.readValue<MutableMap<String, Any>>(json2str)

        val result = isMap(json1, json2, json1)

        val resultstr = mapper.writeValueAsString(result);

        println(resultstr)
    }


    @Suppress("UNCHECKED_CAST")
    private fun isMap(json1: Map<String, *>, json2: Map<String, *>, origin: Map<String, *>): Map<String, *> {

        val com1 = if (json1.size >= json2.size) json1 else json2
        val com2 = if (json1.size >= json2.size) json2 else json1

        val result = HashMap<String, Any>()

        for (key: String in com1.keys) {

            if (com2.containsKey(key)) {


                if (com1[key] is Map<*, *> && com2[key] is Map<*, *>) {

                    result[key] =
                        isMap(com1[key] as Map<String, *>, com2[key] as Map<String, *>, origin[key] as Map<String, *>)

                } else if (com1[key] is List<*> && com2[key] is List<*>) {

                    result[key] = isList(com1[key] as List<*>, com2[key] as List<*>, origin[key] as List<*>)

                } else if (com1[key] is String && com2[key] is String) {

                    result[key] = isString(com1[key] as String, com2[key] as String, origin[key] as String)

                } else { // 프로퍼티는 같지만 타입 불일치
                    result[key] = origin[key].toString() + " # 다른타입"
                }

            } else { // com2에만 있는 프로퍼티
                if (origin.containsKey(key))
                    result[key] = "#json1에만 프로퍼티";
                else
                    result[key] = "#json2에만 프로퍼티";
            }


        }
        return result;
    }

    @Suppress("UNCHECKED_CAST")
    private fun isList(json1: List<*>, json2: List<*>, origin: List<*>): List<*> {

        val result = ArrayList<Any>() // 배열의 순서를 지키기위해 origin을 복사
        result.addAll(origin as Collection<Any>)


        val com1 = if (json1.size >= json2.size) json1 else json2;
        val com2 = if (json1.size >= json2.size) json2 else json1;

        for (index in com1) {


            if (!com2.contains(index)) {

                if (index is Map<*, *>) {
                    for (index2 in com2) {
                        if (index2 is Map<*, *>) {
                            if (index.keys.containsAll(index2.keys)) {
                                result[if (result.indexOf(index) != -1) result.indexOf(index) else result.indexOf(index2)] =
                                    isMap(
                                        index as Map<String, *>,
                                        index2 as Map<String, *>,
                                        origin[if (origin.indexOf(index) != -1) origin.indexOf(index) else origin.indexOf(
                                            index2
                                        )] as Map<String, *>
                                    )
                            } else {
                                if (result.contains(index)) {
                                    result[result.indexOf(index)] = ("$index #json1에만 있는 인덱스");
                                } else {
                                    result.add("$index #json2에만 있는 인덱스");
                                }
                            }
                        }
                    }

                } else
                    if (result.contains(index)) {
                        result[result.indexOf(index)] = ("$index #json1에만 있는 인덱스");
                    } else {
                        result.add("$index #json2에만 있는 인덱스");
                    }

            }
        }



        return result;
    }


    private fun isString(json1: String, json2: String, origin: String): String {
        return if (json1 == json2) {
            json1;
        } else {
            "$origin #다른값 (" + (if (json1 == origin) json2 else json1) + ")";
        }
    }


}