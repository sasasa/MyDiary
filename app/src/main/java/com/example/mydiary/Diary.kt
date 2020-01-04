package com.example.mydiary

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Diary : RealmObject() {
    @PrimaryKey var id: Long = 0
    var title: String = ""
    var bodyText: String = ""
    var date: String = ""
    var image: ByteArray = byteArrayOf()
}