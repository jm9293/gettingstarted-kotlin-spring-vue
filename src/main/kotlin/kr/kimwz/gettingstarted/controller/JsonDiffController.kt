package kr.kimwz.gettingstarted.controller



import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue;
import kr.kimwz.gettingstarted.service.DiffJsonResult
import kr.kimwz.gettingstarted.service.JsonDiffService
import kr.kimwz.gettingstarted.service.LogService

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController



@RestController
class JsonDiffController(val jsonDiffService: JsonDiffService) {

    @PostMapping("/jsondiff")
    fun diff(@RequestBody request: DiffJsonRequest , key : String) : DiffJsonResult {
        return jsonDiffService.compareJson(request.json1 , request.json2 , key)
    }




}

data class DiffJsonRequest(val json1 : String, val json2 : String)
