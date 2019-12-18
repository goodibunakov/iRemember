package ru.goodibunakov.iremember.presentation.bus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    private val bus: PublishSubject<Event> = PublishSubject.create()

    fun post(event: Event) {
        bus.onNext(event)
    }

    fun getEvent(): Observable<Event> {
        return bus
    }
}