package kr.kimwz.gettingstarted.repository

import kr.kimwz.gettingstarted.entity.JsonDiff
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JsonDiffRepository : JpaRepository<JsonDiff , Long> {

    fun findByParam(param : String) : JsonDiff

}