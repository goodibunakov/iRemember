package ru.goodibunakov.iremember.presentation

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    private val bus: PublishSubject<String> = PublishSubject.create()

    fun post(query: String) {
        bus.onNext(query)
    }

    fun getEvent(): Observable<String> {
        return bus
    }
}