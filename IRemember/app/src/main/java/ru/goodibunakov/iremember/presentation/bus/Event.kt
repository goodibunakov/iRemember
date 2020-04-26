package ru.goodibunakov.iremember.presentation.bus

sealed class Event

data class QueryEvent(val query: String) : Event()

object UpdateEvent : Event()

object DeleteAllDoneTasksEvent : Event()