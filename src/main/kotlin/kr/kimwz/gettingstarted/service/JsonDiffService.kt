package kr.kimwz.gettingstarted.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.kimwz.gettingstarted.entity.JsonDiff
import kr.kimwz.gettingstarted.repository.JsonDiffRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class JsonDiffService (val jsonDiffRepository : JsonDiffRepository){

    fun compareJson(Json1 : String , Json2 : String, param : String) : String{
        try {

            val res : JsonDiff? = try {
                jsonDiffRepository.findByParam(param)
            }catch (e : EmptyResultDataAccessException){ // 정보가 아예 없다면
                null
            }


            if(res == null){ // DB에 없는거라면 diff 값저장

                val jsonDiff = JsonDiff()
                val compareResult = compare(Json1 ,Json2);
                jsonDiff.bool = compareResult.bool
                jsonDiff.result = compareResult.result
                jsonDiff.param = param
                println()
                jsonDiffRepository.save(jsonDiff)
            }
            return "OK"

        }catch (e : Exception){ // 변화과 비교 과정에서 오류가 있다면
            return "NO"
        }

    }

    fun findDiffJson(key : String) : DiffJsonResult? {
        val res : JsonDiff? = try {
            jsonDiffRepository.findByParam(key)
        }catch (e : EmptyResultDataAccessException){ // 정보가 아예 없다면
            null
        }

        return if(res != null){
            DiffJsonResult(res.bool.toString(), res.result.toString())
        }else{
            null
        }
    }



    private fun compare(Json1 : String , Json2 : String): DiffJsonResult {

        val mapper = jacksonObjectMapper()
        val json1 = mapper.readValue<MutableMap<String, Any>>(Json1)
        val json2 = mapper.readValue<MutableMap<String, Any>>(Json2)

        val result = mapper.writeValueAsString(isMap(json1, json2, json1));

        return DiffJsonResult((json1==json2).toString() , result);
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

                    result[key] = isType(com1[key] as String, com2[key] as String, origin[key] as String)

                } else if (com1[key] is Boolean && com2[key] is Boolean) {

                    result[key] = isType(com1[key] as Boolean, com2[key] as Boolean, origin[key] as Boolean)

                } else if (com1[key] is Int && com2[key] is Int) {

                    result[key] = isType(com1[key] as Int, com2[key] as Int, origin[key] as Int)

                } else if (com1[key] is Double && com2[key] is Double) {

                    result[key] = isType(com1[key] as Double, com2[key] as Double, origin[key] as Double)

                }  else { // 프로퍼티는 같지만 타입 불일치
                    result[key] = origin[key].toString() + " #다른타입"
                }

            } else { // com2에만 있는 프로퍼티
                if (origin.containsKey(key))
                    result[key] = "#json1에만 있는 프로퍼티";
                else
                    result[key] = "#json2에만 있는 프로퍼티";
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
                    com2@ for (index2 in com2){
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
                                break@com2 // 한번 비교가 끝난 맵은 비교하지 않는다.
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


    private fun  isType(json1: Any, json2: Any, origin: Any): String {
        return if (json1 == json2) {
            json1.toString();
        } else {
            "$origin #다른값 (" + (if (json1 == origin) json2 else json1) + ")";
        }
    }




}

data class DiffJsonResult(val bool : String, val result : String)