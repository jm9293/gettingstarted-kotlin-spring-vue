package kr.kimwz.gettingstarted.entity

import javax.persistence.*

@Table
@Entity
class JsonDiff {

    @Id
    @GeneratedValue
    var id : Long = 0L
    @Column(columnDefinition = "TEXT", nullable = false , length = 64)
    var key : String? = null
    @Column(nullable = false)
    var equalBoolean : Int = 0
    @Column(columnDefinition = "TEXT", nullable = false)
    var json1 : String? = null
    @Column(columnDefinition = "TEXT", nullable = false)
    var json2 : String? = null
    @Column(columnDefinition = "TEXT", nullable = false)
    var result1 : String? =null
    @Column(columnDefinition = "TEXT", nullable = false)
    var result2 : String? =null

}