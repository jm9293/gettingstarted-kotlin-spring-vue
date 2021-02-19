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

        }catch (e : Exception){ // 변화와 비교 과정에서 오류가 있다면
            e.printStackTrace()
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
        var json1 : Any? = null
        var json2 : Any? = null
        var result : String? = null
        if(Json1[0] == '{' && Json1[Json1.length-1] =='}' && Json2[0] == '{' && Json2[Json2.length-1] =='}'){ // 비교대상이 맵일때
            json1 = mapper.readValue<MutableMap<String, Any>>(Json1)
            json2 = mapper.readValue<MutableMap<String, Any>>(Json2)
            result = mapper.writeValueAsString(isMap(json1, json2, json1));
        }else if(Json1[0] == '[' && Json1[Json1.length-1] ==']' && Json2[0] == '[' && Json2[Json2.length-1] ==']'){ // 비교대상이 어레이일때
            json1 = mapper.readValue<List<Any>>(Json1)
            json2 = mapper.readValue<List<Any>>(Json2)
            result = mapper.writeValueAsString(isList(json1, json2, json1));
        }else{ // body 타입 미스매치
            return DiffJsonResult("false" , "bodyTypeMissMatch");
        }




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

        //result.addAll(origin as Collection<Any>)

        val com1 : ArrayList<Any> = if (json1.size >= json2.size) json1 as ArrayList<Any> else json2 as ArrayList<Any>;
        val com2 : ArrayList<Any> = if (json1.size >= json2.size) json2 as ArrayList<Any> else json1 as ArrayList<Any>;

        val com2Buf : ArrayList<Any> = ArrayList()
        val originBuf : ArrayList<Any> = ArrayList()
        val originRemove : ArrayList<Any> = ArrayList()
        originBuf.addAll(origin as Collection<Any>)
        com2Buf.addAll(com2) //com2 복제

        val comAll : ArrayList<Any> = ArrayList()

        for (index in com1){ // 중복인값 쌍당 1개씩만 com2buf에서 날리기
            if(com2.indexOf(index) != -1){
                com2Buf.remove(index)
            }
        }

        println(com1)
        println(com2Buf)
        comAll.addAll(com1)
        comAll.addAll(com2Buf)

        for (index in comAll){
            if (index is Map<*, *>) { // 맵이라면
                for (index2 in comAll){
                    if (index != index2 &&
                        index2 is Map<*, *> &&
                        if(index.keys.size > index2.keys.size)index.keys.containsAll(index2.keys)else index2.keys.containsAll(index.keys)) { // key 값이 많은 기준으로 부분집합인지 확인한다.
                        isMap(
                            index as Map<String, *> , index2 as Map<String, *> ,
                            origin[if (origin.indexOf(index) != -1) origin.indexOf(index) else origin.indexOf(index2)] as Map<String, *>
                        )
                        comAll.remove(index) // 값을 지운다
                        comAll.remove(index2) // 값을 지운다
                    }

                }


            }else if(index is List<*>){ // 리스트라면

                for (index2 in comAll){
                    if (index != index2 && index2 is List<*> &&
                        if(index.size > index2.size)index.containsAll(index2) else index2.containsAll(index)){ // 인덱스 값이 많은 기준으로 부분집합인지 확인한다.
                        isList(index , index2 ,  origin[if (origin.indexOf(index) != -1) origin.indexOf(index) else origin.indexOf(index2)] as List<*>)
                        comAll.remove(index) // 값을 지운다
                        comAll.remove(index2)
                    }
                }

            }else { // 맵도아니고 리스트도 아니기때문에 공통값이거나 다른값이다.
                println(index)
                println(originBuf)
                println(com2Buf)
                println(result)
                println()
                if (!(origin.contains(index) && com1.contains(index) && com2.contains(index) && !originRemove.contains(index))){ // 중복이아니거나 한번 중복이 존재하는 숫자라면
                    if(originBuf.contains(index) && !com2Buf.contains(index)){ // result가 오리진을 기준으로 만들었으므로
                        result.add("$index #json1에만 있는 인덱스");
                    }else if(originBuf.contains(index) && com2Buf.contains(index)){
                        result.add("$index #json1에만 있는 인덱스");
                    }else{
                        result.add("$index #json2에만 있는 인덱스");
                    }
                }else{
                    result.add(index)
                }
                originRemove.add(index)
                originBuf.remove(index)

            }





        }




//            if(com2.contains(index)){
//                continue;
//            }
//            if (index is Map<*, *>) {
//                    com2@ for (index2 in com2){
//                        if (index2 is Map<*, *>) {
//                            if (index.keys.containsAll(index2.keys)) {
//                                result[if (result.indexOf(index) != -1) result.indexOf(index) else result.indexOf(index2)] =
//                                    isMap(
//                                        index as Map<String, *>,
//                                        index2 as Map<String, *>,
//                                        origin[if (origin.indexOf(index) != -1) origin.indexOf(index) else origin.indexOf(
//                                            index2
//                                        )] as Map<String, *>
//                                    )
//                                break@com2 // 한번 비교가 끝난 맵은 비교하지 않는다.
//                            } else {
//                                if (result.contains(index)) {
//                                    result[result.indexOf(index)] = ("$index #json1에만 있는 인덱스");
//                                } else {
//                                    result.add("$index #json2에만 있는 인덱스");
//                                }
//                            }
//                        }
//                    }
//
//
//                }else{
//
//                }




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