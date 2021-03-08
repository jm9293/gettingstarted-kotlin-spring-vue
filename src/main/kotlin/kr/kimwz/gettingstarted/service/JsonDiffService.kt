package kr.kimwz.gettingstarted.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.kimwz.gettingstarted.entity.JsonDiff
import kr.kimwz.gettingstarted.repository.JsonDiffRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class JsonDiffService(val jsonDiffRepository: JsonDiffRepository) {

    val resultMap = HashMap<String, DiffJsonResult>()

    fun compareJson(json1: String, json2: String, key: String): Boolean {

        if (!resultMap.containsKey(key)) { // resultMap에 값이 없다면
            try {
                val res: JsonDiff? = try {
                    jsonDiffRepository.findByKey(key)
                } catch (e: EmptyResultDataAccessException) { // 정보가 아예 없다면
                    null
                }


                if (res == null) { // DB에 없는거라면 diff 값저장

                    val jsonDiff = JsonDiff()
                    val compareResult = compare(json1, json2)

                    jsonDiff.equalBoolean = compareResult.bool
                    jsonDiff.json1 = compareResult.json1
                    jsonDiff.json2 = compareResult.json2
                    jsonDiff.result1 = compareResult.result1
                    jsonDiff.result2 = compareResult.result2
                    jsonDiff.key = key

                    jsonDiffRepository.save(jsonDiff)
                    resultMap[key] = compareResult

                }

                return true

            } catch (e: Exception) { // 변화와 비교 과정에서 오류가 있다면

                e.printStackTrace()

                return false
            }

        } else
            return true

    }

    fun findDiffJson(key: String): DiffJsonResult? {

        if (!resultMap.containsKey(key)) { // resultMap에 값이 없다면

            val res: JsonDiff? = try {
                jsonDiffRepository.findByKey(key)
            } catch (e: EmptyResultDataAccessException) { // 정보가 아예 없다면
                null
            }

            return if (res != null)
                DiffJsonResult(res.equalBoolean, res.json1, res.json2, res.result1.toString(), res.result2.toString())
            else {
                null
            }

        } else {

            val res: DiffJsonResult? = resultMap[key]

            return if (res != null)
                DiffJsonResult(res.bool, res.json1, res.json2, res.result1, res.result2)
            else
                null

        }

    }


    private fun compare(jsonStr1: String, jsonStr2: String): DiffJsonResult {

        val mapper = jacksonObjectMapper()
        val diffJsonResult = DiffJsonResult()
        val diffOffSet1 = DiffOffSet(1)
        val diffOffSet2 = DiffOffSet(2)

        if (jsonStr1[0] == '{' && jsonStr1[jsonStr1.length - 1] == '}' && jsonStr2[0] == '{' && jsonStr2[jsonStr2.length - 1] == '}') { // 비교대상이 맵일때

            val json1 = mapper.readValue<MutableMap<String, Any>>(jsonStr1)
            val json2 = mapper.readValue<MutableMap<String, Any>>(jsonStr2)

            compareMap("", json1, json2, diffOffSet1)
            compareMap("", json2, json1, diffOffSet2)

            diffJsonResult.bool = if (json1 == json2) 1 else 0
            diffJsonResult.json1 = jsonStr1
            diffJsonResult.json2 = jsonStr2
            diffJsonResult.result1 = mapper.writeValueAsString(diffOffSet1.diffData)
            diffJsonResult.result2 = mapper.writeValueAsString(diffOffSet2.diffData)


        } else if (jsonStr1[0] == '[' && jsonStr1[jsonStr1.length - 1] == ']' && jsonStr2[0] == '[' && jsonStr2[jsonStr2.length - 1] == ']') { // 비교대상이 어레이일때

            val json1 = mapper.readValue<List<Any>>(jsonStr1)
            val json2 = mapper.readValue<List<Any>>(jsonStr2)

            compareList("", json1, json2, diffOffSet1)
            compareList("", json2, json1, diffOffSet2)

            diffJsonResult.json1 = jsonStr1
            diffJsonResult.json2 = jsonStr2
            diffJsonResult.bool = if (json1 == json2) 1 else 0
            diffJsonResult.result1 = mapper.writeValueAsString(diffOffSet1.diffData)
            diffJsonResult.result2 = mapper.writeValueAsString(diffOffSet2.diffData)

        }

        return diffJsonResult

    }


    private fun compareMap(path: Any, json1: Map<*, *>, json2: Map<*, *>, diffOffSet: DiffOffSet) {

        diffOffSet.spotStack.push(path)

        val origin = json1 as Map<String, Any?>

        json2 as Map<String, Any?>

        val com1: Map<String, Any?> = if (json1.size >= json2.size) json1 else json2
        val com2: Map<String, Any?> = if (json1.size >= json2.size) json2 else json1


        for (key: String in com1.keys) {

            if (com2.containsKey(key)) {

                if (com1[key] == null && com2[key] == null) continue
                else if ((com1[key] == null || com2[key] == null) || (com1[key]!!::class != com2[key]!!::class)) { // 프로퍼티는 같지만 타입 불일치

                    val another: Any? = if (origin == com1) com2[key] else com1[key]
                    createdResult(key, json1[key], another, 2, diffOffSet)

                } else when {

                    com1[key] is Map<*, *> -> compareMap(
                        key,
                        json1[key] as Map<String, Any?>,
                        json2[key] as Map<String, Any?>,
                        diffOffSet
                    )
                    com1[key] is List<*> -> compareList(
                        key,
                        json1[key] as List<Any?>,
                        json2[key] as List<Any?>,
                        diffOffSet
                    )
                    else -> compareValue(key, json1[key], json2[key], diffOffSet) as Any

                }

            } else {
                if (origin.containsKey(key))
                    createdResult(key, origin[key], null, 3, diffOffSet)
            }

        }

        diffOffSet.spotStack.pop()

    }


    private fun compareList(path: Any, json1: List<*>, json2: List<*>, diffOffSet: DiffOffSet) {

        diffOffSet.spotStack.push(path)

        val maxSize = if (json1.size > json2.size) json1.size else json2.size

        for (i in 0..maxSize) {

            if (json1.size > i && json2.size > i) {

                if (json1[i] == null && json2[i] == null) continue
                else if ((json1[i] == null || json2[i] == null) || (json1[i]!!::class != json2[i]!!::class))  // 인덱스 끼리 타입 불일치
                    createdResult(i, json1[i], json2[i], 2, diffOffSet)
                else when {
                    json1[i] is Map<*, *> -> compareMap(
                        i,
                        json1[i] as Map<String, Any?>,
                        json2[i] as Map<String, Any?>,
                        diffOffSet
                    )
                    json1[i] is List<*> -> compareList(i, json1[i] as List<Any?>, json2[i] as List<Any?>, diffOffSet)
                    else -> compareValue(i, json1[i], json2[i], diffOffSet) as Any
                }

            } else {
                if (json1.size > i)
                    createdResult(i, json1[i], null, 4, diffOffSet)
            }

        }

        diffOffSet.spotStack.pop()

    }


    private fun compareValue(path: Any, json1: Any?, json2: Any?, diffOffSet: DiffOffSet): Any? {
        return if (json1 == json2) {
            json1
        } else {
            createdResult(path, json1, json2, 1, diffOffSet)
        }
    }


    private fun getType(another: Any?): String {
        return when (another) {
            is Map<*, *> -> "Object"
            is List<*> -> "Array"
            is Number -> "Number"
            is String -> "String"
            null -> "Null"
            else -> "Boolean"
        }
    }


    private fun createdResult(
        path: Any,
        value: Any?,
        diffValue: Any?,
        case: Int,
        diffOffSet: DiffOffSet
    ) {
        diffOffSet.spotStack.push(path)

        val resultMap = HashMap<String, Any?>()
        val spot = diffOffSet.spotStack.joinToString("/")
        val anotherOffset = if (diffOffSet.offset == 1) 2 else 1

        resultMap["spot"] = spot
        resultMap["case"] = case
        resultMap["message"] =
            when (case) {
                1 -> "json${diffOffSet.offset} : (${value}) 값이 다릅니다. \njson${anotherOffset} : (${diffValue})"
                2 -> "json${diffOffSet.offset} : (${value}) 타입이 다릅니다. \njson${anotherOffset} : (${getType(diffValue)})"
                3 -> "$path+ : json${diffOffSet.offset} 에만 있는 프로퍼티"
                4 -> "$path+ : json${diffOffSet.offset} 에만 있는 인덱스"
                else -> "No Message"
            }
        diffOffSet.diffData[spot] = resultMap
        diffOffSet.spotStack.pop()
    }


}

data class DiffJsonResult(
    var bool: Int = 0,
    var json1: String? = null,
    var json2: String? = null,
    var result1: String = "bodyTypeMissMatch",
    var result2: String = "bodyTypeMissMatch"
)

data class DiffOffSet(val offset: Int) {
    val spotStack: Stack<Any> = Stack()
    val diffData: HashMap<String, Any> = HashMap()
}