package ru.goodibunakov.iremember.presentation

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    private val bus: PublishSubject<String> = PublishSubject.create()
    private var queryString : StringBuilder = java.lang.StringBuilder("")

    fun post(query: String) {
        queryString.clear()
        queryString.append(query)
        Log.d("rx", "set query to bus $query")
        bus.onNext(query)
    }

    fun getEvent(): Observable<String> {
        return bus
    }

    fun getQueryString(): String {
        Log.d("debug", "getQueryString() = $queryString")
        return queryString.toString()
    }
}