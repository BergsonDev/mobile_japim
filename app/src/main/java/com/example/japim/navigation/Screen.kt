package com.example.japim.navigation

sealed class Screen(val route: String) {

    // General
    object Login : Screen("login")
    object Home : Screen("home")
    object RegisterAccount : Screen("register_account")

    // Events
    object Events : Screen("events")

    object EventDetails : Screen("event_details/{event_id}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }

    object RegisterEvent : Screen("register_event")

    object UpdateEvent : Screen("register_event/{event_id}") {
        fun createRoute(eventId: String) = "register_event/$eventId"
    }

    // User
    object MyCertificates : Screen("my_certificates")

    object CertificateDetails : Screen("certificate_details/{certificate_id}") {
        fun createRoute(certificateId: String) =
            "certificate_details/$certificateId"
    }

    object ParticipatingEvents : Screen("participating_events")

    // Administration
    object Admin : Screen("admin")
    object RegisterUser : Screen("register_user")
    object ManageUsers : Screen("manage_users")
    object ManageEvents : Screen("manage_events")
    object ManageRoles : Screen("manage_roles")
}