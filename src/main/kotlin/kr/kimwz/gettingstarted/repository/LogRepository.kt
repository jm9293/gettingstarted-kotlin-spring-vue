package kr.kimwz.gettingstarted.repository

import kr.kimwz.gettingstarted.entity.Log
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LogRepository: JpaRepository<Log, Long> {
}
