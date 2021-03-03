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
class JsonDiffService(val jsonDiffRepository: JsonDiffRepository) {

    fun compareJson(Json1: String, Json2: String, param: String): String {
        try {

            val res: JsonDiff? = try {
                jsonDiffRepository.findByParam(param)
            } catch (e: EmptyResultDataAccessException) { // 정보가 아예 없다면
                null
            }


            if (res == null) { // DB에 없는거라면 diff 값저장

                val jsonDiff = JsonDiff()
                val compareResult = compare(Json1, Json2)

                jsonDiff.bool = compareResult.bool
                jsonDiff.result1 = compareResult.result1
                jsonDiff.result2 = compareResult.result2
                jsonDiff.param = param

                jsonDiffRepository.save(jsonDiff)

            }

            return "OK"

        } catch (e: Exception) { // 변화와 비교 과정에서 오류가 있다면

            e.printStackTrace()

            return "NO"
        }

    }

    fun findDiffJson(key: String): DiffJsonResult? {
        val res: JsonDiff? = try {
            jsonDiffRepository.findByParam(key)
        } catch (e: EmptyResultDataAccessException) { // 정보가 아예 없다면
            null
        }

        return if (res != null) {
            DiffJsonResult(res.bool.toString(), res.result1.toString(), res.result2.toString())
        } else {
            null
        }
    }


    private fun compare(Json1: String, Json2: String): DiffJsonResult {

        val mapper = jacksonObjectMapper()
        val diffJsonResult = DiffJsonResult()


        if (Json1[0] == '{' && Json1[Json1.length - 1] == '}' && Json2[0] == '{' && Json2[Json2.length - 1] == '}') { // 비교대상이 맵일때

            val json1 = mapper.readValue<MutableMap<String, Any>>(Json1)
            val json2 = mapper.readValue<MutableMap<String, Any>>(Json2)

            diffJsonResult.bool = (json1 == json2).toString()
            diffJsonResult.result1 = mapper.writeValueAsString(compareMap(json1, json2, 1))
            diffJsonResult.result2 = mapper.writeValueAsString(compareMap(json2, json1, 2))

        } else if (Json1[0] == '[' && Json1[Json1.length - 1] == ']' && Json2[0] == '[' && Json2[Json2.length - 1] == ']') { // 비교대상이 어레이일때

            val json1 = mapper.readValue<List<Any>>(Json1)
            val json2 = mapper.readValue<List<Any>>(Json2)

            diffJsonResult.bool = (json1 == json2).toString()
            diffJsonResult.result1 = mapper.writeValueAsString(compareList(json1, json2, 1))
            diffJsonResult.result2 = mapper.writeValueAsString(compareList(json2, json1, 2))

        }

        return diffJsonResult;

    }


    private fun compareMap(json1: Map<*, *>, json2: Map<*, *>, offset: Int): Map<String, *> {

        val origin = json1 as Map<String, Any?>
        json2 as Map<String, Any?>

        val com1: Map<String, Any?> = if (json1.size >= json2.size) json1 else json2
        val com2: Map<String, Any?> = if (json1.size >= json2.size) json2 else json1

        val result = HashMap<String, Any>()

        for (key: String in com1.keys) {

            if (com2.containsKey(key)) {

                if (com1[key] == null && com2[key] == null) {

                    result[key] = "#null" //null인글자가 있을수 있으므로

                } else if ((com1[key] == null || com2[key] == null) || (com1[key]!!::class != com2[key]!!::class)) { // 프로퍼티는 같지만 타입 불일치

                    val type: String

                    val another: Any? = if (origin == com1) com2[key] else com1[key]

                    type = when (another) {
                        is Map<*, *> -> "Object"
                        is List<*> -> "Array"
                        is Number -> "Number"
                        is String -> "String"
                        null -> "Null"
                        else -> "Boolean"
                    }

                    result[key] = origin[key].toString() + " #다른타입 (" + type + ")"

                } else if (com1[key] is Map<*, *>) {

                    result[key] = compareMap(json1[key] as Map<String, Any?>, json2[key] as Map<String, Any?>, offset)

                } else if (com1[key] is List<*>) {

                    result[key] = compareList(json1[key] as List<Any?>, json2[key] as List<Any?>, offset)

                } else {

                    result[key] = compareValue(json1[key], json2[key]) as Any

                }

            } else {

                if (origin.containsKey(key))

                    result[key] = when {
                        com1[key] is List<*> -> "[...] #json" + offset + "에만 있는 프로퍼티"
                        com1[key] is Map<*, *> -> "{...} #json" + offset + "에만 있는 프로퍼티"
                        com1[key] == null -> "null #json" + offset + "에만 있는 프로퍼티"
                        else -> com1[key].toString() + " #json" + offset + "에만 있는 프로퍼티"
                    }

            }


        }

        return result;
    }


//    private fun compareList2(json1: List<*>, json2: List<*>, offset: Int): List<*> {
//
//        val result = ArrayList<Any>()
//
//        val com1: ArrayList<Any> = if (json1.size >= json2.size) json1 as ArrayList<Any> else json2 as ArrayList<Any>
//        val com2: ArrayList<Any> = if (json1.size >= json2.size) json2 as ArrayList<Any> else json1 as ArrayList<Any>
//
//        val com2Buf: ArrayList<Any> = ArrayList()
//        val originBuf: ArrayList<Any> = ArrayList()
//        val originRemove: ArrayList<Any> = ArrayList()
//
//        originBuf.addAll(listOf(json1))
//        com2Buf.addAll(com2) //com2 복제
//
//        val comAll: ArrayList<Any> = ArrayList()
//
//        for (index in com1) { // 중복인값 쌍당 1개씩만 com2buf 에서 날리기
//            if (com2.indexOf(index) != -1) com2Buf.remove(index)
//        }
//
//        comAll.addAll(com1)
//        comAll.addAll(com2Buf)
//
//        val comAllIter: MutableIterator<Any> = comAll.iterator() // 이터레이터 생성
//
//
//        while (comAllIter.hasNext()) {
//
//            val index = comAllIter.next();
//
//            if (index is Map<*, *>) { // 맵이라면
//
//                val comAllIter2: MutableIterator<Any> = comAll.iterator() //또다른 이터레이터 생성
//
//                while (comAllIter2.hasNext()) {
//
//                    val index2 = comAllIter2.next();
//
//                    if (index != index2 && index2 is Map<*, *>) {
//
//                        val isOverlap =
//                            if (index.keys.size > index2.keys.size) (index.keys intersect index2.keys).isEmpty()
//                            else (index2.keys intersect index.keys).isEmpty()
//
//                        if (isOverlap) {
//
//                            val json1Index = if (json1.contains(index)) index else index2
//                            val json2Index = if (json2.contains(index)) index else index2
//
//                            result.add(
//                                compareMap(
//                                    json1Index as Map<String, Any?>,
//                                    json2Index as Map<String, Any?>,
//                                    offset
//                                )
//                            )
//
//                            comAll.remove(index) // 다시 비교 되지 않도록 삭제
//                        }
//
//                    }
//
//                }
//
//
//            } else if (index is List<*>) { // 리스트라면
//
//                val comAllIter2: MutableIterator<Any> = comAll.iterator() // 또다른 이터레이터 생성
//
//                while (comAllIter2.hasNext()) {
//
//                    val index2 = comAllIter2.next();
//
//                    if (index != index2 && index2 is List<*> &&
//                        if (index.size > index2.size) index.containsAll(index2) else index2.containsAll(index)
//                    ) { // 인덱스 값이 많은 기준으로 부분집합인지 확인한다.
//
//                        val json1Index = if (json1.contains(index)) index else index2
//                        val json2Index = if (json2.contains(index)) index else index2
//
//                        result.add(
//                            compareList(
//                                json1Index as List<Any>,
//                                json2Index as List<Any>,
//                                offset
//                            )
//                        )
//                        comAll.remove(index) // 다시 비교 되지 않도록 삭제
//                    }
//                }
//
//                if (json1.contains(index) && comAll.contains(index)) { // 원본에 존재하고 아직 공통배열에 남아있다면 원본에만 있는 리스트로 간
//                    result.add("$index #json$offset 에만 있는 인덱스");
//                }
//
//            } else { // 맵도아니고 리스트도 아니기때문에 공통값이거나 다른값이다.
//
//                if (!(json1.contains(index) && com1.contains(index) && com2.contains(index) && !originRemove.contains(
//                        index
//                    ))
//                ) { // 중복이아니거나 한번 중복이 존재하는 숫자라면
//                    if (originBuf.contains(index)) {
//                        result.add("$index #json$offset 에만 있는 인덱스");
//                    }
//                } else {
//                    result.add(index)
//                }
//                originRemove.add(index)
//                originBuf.remove(index)
//
//            }
//
//
//        }
//
//        return result;
//    }


    private fun compareList(json1: List<*>, json2: List<*>, offset: Int) : List<*> {

        val maxSize = if (json1.size > json2.size) json1.size else json2.size

        val result = ArrayList<Any>()

        for (i in 0..maxSize) {

            if (json1.size > i && json2.size > i) {

                if (json1[i] == null && json2[i] == null) {

                    result.add("#null") //null인글자가 있을수 있으므로

                } else if ((json1[i] == null || json2[i] == null) || (json1[i]!!::class != json2[i]!!::class)) { // 프로퍼티는 같지만 타입 불일치

                    val type = getAnotherType(json2[i])

                    result.add(json1[i].toString() + " #다른타입 (" + type + ")")

                } else if (json1[i] is Map<*, *>) {

                    result.add(compareMap(json1[i] as Map<String, Any?>, json2[i] as Map<String, Any?>, offset))

                } else if (json1[i] is List<*>) {

                    result.add(compareList(json1[i] as List<Any?>, json2[i] as List<Any?>, offset))

                } else {

                    result.add(compareValue(json1[i], json2[i]) as Any)

                }

            } else {

                if (json1.size > i) {

                    result.add(json1[i].toString() + "#json$offset 에만 있는 인덱스")

                }

            }

        }

        return result

    }


    private fun compareValue(json1: Any?, json2: Any?): Any? {
        return if (json1 == json2) {
            json1
        } else {
            "$json1 #다른값 ($json2)";
        }
    }

    private fun getAnotherType(another: Any?): String {
        return when (another) {
            is Map<*, *> -> "Object"
            is List<*> -> "Array"
            is Number -> "Number"
            is String -> "String"
            null -> "Null"
            else -> "Boolean"
        }
    }


}

data class DiffJsonResult(
    var bool: String = "false",
    var result1: String = "bodyTypeMissMatch",
    var result2: String = "bodyTypeMissMatch"
)