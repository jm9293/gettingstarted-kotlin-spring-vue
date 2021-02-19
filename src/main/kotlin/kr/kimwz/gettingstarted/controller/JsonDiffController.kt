package kr.kimwz.gettingstarted.controller



import kr.kimwz.gettingstarted.service.DiffJsonResult
import kr.kimwz.gettingstarted.service.JsonDiffService
import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController



@RestController
class JsonDiffController(val jsonDiffService: JsonDiffService) {

    @PostMapping("/jsondiff")
    fun saveDiff(@RequestBody request: DiffJsonRequest , key : String) : String {

        if(key.isEmpty()){
            return "NO"
        }

        return jsonDiffService.compareJson(request.json1 , request.json2 , key)
    }

    @GetMapping("/jsondiff")
    fun findDiff(key : String) : DiffJsonResponse{

        if(key.isEmpty()){
            return DiffJsonResponse("NO",null , null)
        }

        val res = jsonDiffService.findDiffJson(key)

        return if(res!=null){
            DiffJsonResponse("OK", res.bool , res.result)
        }else{
            DiffJsonResponse("NO",null , null)
        }
    }




}

data class DiffJsonRequest(val json1 : String, val json2 : String)
data class DiffJsonResponse(val status : String ,val bool : String?, val result : String?)