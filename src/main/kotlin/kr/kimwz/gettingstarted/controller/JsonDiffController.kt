package kr.kimwz.gettingstarted.controller


import kr.kimwz.gettingstarted.service.JsonDiffService
import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController



@RestController
class JsonDiffController(val jsonDiffService: JsonDiffService) {

    val okResponse = DiffJsonResponse("OK", null, null, null, null, null)
    val noResponse = DiffJsonResponse("NO", null, null, null, null, null)


    @PostMapping("/jsondiff")
    fun saveDiff(@RequestBody request: DiffJsonRequest, key: String): DiffJsonResponse {

        return if (key.isEmpty() || !jsonDiffService.compareJson(request.json1, request.json2, key))
            noResponse
        else
            okResponse

    }

    @GetMapping("/jsondiff")
    fun findDiff(key: String): DiffJsonResponse {

        if (key.isEmpty()) {
            return noResponse
        }

        val res = jsonDiffService.findDiffJson(key)

        return if (res != null) {
            DiffJsonResponse("OK", res.bool, res.json1, res.json2, res.result1, res.result2)
        } else {
            noResponse
        }
    }


}

data class DiffJsonRequest(val json1: String, val json2: String)
data class DiffJsonResponse(
    val status: String,
    val equalBool: Int?,
    val json1: String?,
    val json2: String?,
    val result1: String?,
    val result2: String?
)