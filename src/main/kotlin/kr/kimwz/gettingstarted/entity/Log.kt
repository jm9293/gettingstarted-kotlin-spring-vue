package kr.kimwz.gettingstarted.entity

import org.hibernate.annotations.Type
import java.time.ZonedDateTime
import javax.persistence.*

@Table
@Entity
class Log {
    @Id
    @GeneratedValue
    var id: Long = 0L
    var payload: String? = null
    @Type(type = "org.hibernate.type.ZonedDateTimeType")
    var createTime: ZonedDateTime = ZonedDateTime.now()
}
