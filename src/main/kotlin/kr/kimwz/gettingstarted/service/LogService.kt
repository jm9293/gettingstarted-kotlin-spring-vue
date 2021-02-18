package kr.kimwz.gettingstarted.service

import kr.kimwz.gettingstarted.entity.Log
import kr.kimwz.gettingstarted.repository.LogRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class LogService(val logRepository: LogRepository) {
    @Transactional
    fun saveLog(payload: String): Log {
        val log = Log()
        log.payload = payload
        logRepository.save(log)
        return log
    }

    fun getAllLogs(): List<Log> {
        return logRepository.findAll()
    }
}
