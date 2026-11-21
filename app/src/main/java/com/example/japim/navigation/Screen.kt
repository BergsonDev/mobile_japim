package com.example.japim.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RegisterAccount : Screen("registerAccount")
    object EventDetails : Screen("event_details/{event_id}") {
        fun createRoute(event_id: String) = "event_details/$event_id"
    }

    object Events : Screen("events")

    object RequesterPresence : Screen("requester_presence/{event_id}"){
        fun createRoute(event_id: String) = "requester_presence/$event_id"
    }
}