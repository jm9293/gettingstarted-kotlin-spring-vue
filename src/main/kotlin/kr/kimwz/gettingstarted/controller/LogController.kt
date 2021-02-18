package kr.kimwz.gettingstarted.controller

import kr.kimwz.gettingstarted.entity.Log
import kr.kimwz.gettingstarted.service.LogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LogController(val logService: LogService) {

    @GetMapping("/logs")
    fun getAllLogs(): LogsResponse {
        val logs = logService.getAllLogs()
        return LogsResponse(logs)
    }

    @PostMapping("/logs")
    fun saveNewLog(@RequestBody request: LogRequest): LogResponse {
        val log = logService.saveLog(request.payload)
        return LogResponse(log)
    }
}
data class LogRequest(val payload: String)
data class LogsResponse(val logs: List<Log>)
data class LogResponse(val log: Log)
