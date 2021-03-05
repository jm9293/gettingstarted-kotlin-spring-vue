package kr.kimwz.gettingstarted.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.kimwz.gettingstarted.entity.JsonDiff
import kr.kimwz.gettingstarted.repository.JsonDiffRepository
import org.springframework.context.annotation.Scope
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.*
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
            diffJsonResult.result1 = mapper.writeValueAsString(compareMap("" , json1, json2, 1, Stack<Any>()))
            diffJsonResult.result2 = mapper.writeValueAsString(compareMap("" , json2, json1, 2, Stack<Any>()))

        } else if (Json1[0] == '[' && Json1[Json1.length - 1] == ']' && Json2[0] == '[' && Json2[Json2.length - 1] == ']') { // 비교대상이 어레이일때

            val json1 = mapper.readValue<List<Any>>(Json1)
            val json2 = mapper.readValue<List<Any>>(Json2)

            diffJsonResult.bool = (json1 == json2).toString()
            diffJsonResult.result1 = mapper.writeValueAsString(compareList("" , json1, json2, 1, Stack<Any>()))
            diffJsonResult.result2 = mapper.writeValueAsString(compareList("" , json2, json1, 2, Stack<Any>()))

        }

        return diffJsonResult

    }


    private fun compareMap(path: Any, json1: Map<*, *>, json2: Map<*, *>, offset: Int, spotStack: Stack<Any>): Map<String, *> {

        spotStack.push(path)

        val origin = json1 as Map<String, Any?>

        json2 as Map<String, Any?>

        val com1: Map<String, Any?> = if (json1.size >= json2.size) json1 else json2
        val com2: Map<String, Any?> = if (json1.size >= json2.size) json2 else json1

        val result = HashMap<String, Any?>()

        for (key: String in com1.keys) {

            if (com2.containsKey(key)) {

                if (com1[key] == null && com2[key] == null) {
                    result[key] = null
                } else if ((com1[key] == null || com2[key] == null) || (com1[key]!!::class != com2[key]!!::class)) { // 프로퍼티는 같지만 타입 불일치

                    val another: Any? = if (origin == com1) com2[key] else com1[key]
                    val type = getType(another)

                    result[key] = createdResult(key, json1[key], null, "다른타입 ($type)","blue", spotStack)

                } else if (com1[key] is Map<*, *>) {
                    result[key] =
                        compareMap(key , json1[key] as Map<String, Any?>, json2[key] as Map<String, Any?>, offset, spotStack)
                } else if (com1[key] is List<*>) {
                    result[key] =
                        compareList(key , json1[key] as List<Any?>, json2[key] as List<Any?>, offset, spotStack)
                } else {
                    result[key] =
                        compareValue(key ,json1[key], json2[key] , spotStack) as Any
                }

            } else {
                if (origin.containsKey(key))
                    result[key] = createdResult(key , origin[key], null, "json" + offset + "에만 있는 프로퍼티","pink", spotStack)
            }

        }

        spotStack.pop()

        return result;
    }





    private fun compareList(path : Any ,json1: List<*>, json2: List<*>, offset: Int, spotStack: Stack<Any>): List<*> {

        spotStack.push(path)

        val maxSize = if (json1.size > json2.size) json1.size else json2.size

        val result = ArrayList<Any?>()

        for (i in 0..maxSize) {

            if (json1.size > i && json2.size > i) {

                if (json1[i] == null && json2[i] == null) {
                    result.add(null)
                } else if ((json1[i] == null || json2[i] == null) || (json1[i]!!::class != json2[i]!!::class)) { // 인덱스 끼리 타입 불일치
                    result.add(createdResult(i, json1[i], null, "다른타입 ("+getType(json2[i])+")", "blue" ,spotStack))
                } else if (json1[i] is Map<*, *>) {
                    result.add(compareMap(i, json1[i] as Map<String, Any?>, json2[i] as Map<String, Any?>, offset, spotStack))
                } else if (json1[i] is List<*>) {
                    result.add(compareList(i, json1[i] as List<Any?>, json2[i] as List<Any?>, offset, spotStack))
                } else {
                    result.add(compareValue(i, json1[i], json2[i] , spotStack) as Any)
                }

            } else {
                if (json1.size > i)
                    result.add(createdResult(i ,json1[i], null, "json$offset 에만 있는 인덱스", "pink" ,spotStack))
            }

        }

        spotStack.pop()

        return result

    }


    private fun compareValue(path: Any , json1: Any?, json2: Any? , spotStack: Stack<Any>): Any? {
        return if (json1 == json2) {
            json1
        } else {
            createdResult(path ,json1, json2, "다른 값" , "green",spotStack)
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


    private fun createdResult(path: Any ,value: Any?, diffValue: Any?, message: String, color : String, spotStack: Stack<Any>): Map<*, *> {

        val resultMap = HashMap<String, Any?>()

        spotStack.push(path)

        resultMap["value"] = value
        resultMap["diffValue"] = diffValue
        resultMap["message"] = message
        resultMap["spot"] = getSpotToString(spotStack);
        resultMap["backgroundColor"] = color
        println(resultMap["spot"].toString() + " | " +resultMap["message"].toString())

        spotStack.pop()

        return resultMap

    }

    private fun getSpotToString(spotStack: Stack<Any>): String {

        var result = ""

        for (spot in spotStack) {
            result += "$spot/"
        }

        return result

    }


}

data class DiffJsonResult(
    var bool: String = "false",
    var result1: String = "bodyTypeMissMatch",
    var result2: String = "bodyTypeMissMatch"
)