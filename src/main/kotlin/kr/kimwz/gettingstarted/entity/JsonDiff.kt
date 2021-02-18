package kr.kimwz.gettingstarted.entity

import javax.persistence.*

@Table
@Entity
class JsonDiff {

    @Id
    @GeneratedValue
    var id : Long = 0L
    @Column(columnDefinition = "TEXT", nullable = false)
    var param : String? = null
    @Column(nullable = false)
    var bool : String? = null
    @Column(columnDefinition = "TEXT", nullable = false)
    var result : String? =null

}