package com.example.japim.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object RegisterAccount : Screen("registerAccount")
    object EventDetails : Screen("event_details/{event_id}") {
        fun createRoute(event_id: String) = "event_details/$event_id"
    }

    object Events : Screen("events")

    object RegisterEvent : Screen("register_event")

    object UpdateEvent : Screen("register_event/{event_id}") {
        fun createRoute(eventId: String) = "register_event/$eventId"
    }

    object RequesterPresence : Screen("requester_presence/{event_id}"){
        fun createRoute(event_id: String) = "requester_presence/$event_id"
    }

    object ValidateRequesterPresence : Screen("validate_requester_presence/{event_id}"){
        fun createRoute(event_id: String) = "validate_requester_presence/$event_id"
    }

    object ListPresencesInEvent : Screen("event_presences/{event_id}"){
        fun createRoute(event_id: String) = "event_presences/$event_id";
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

    object MyEvents : Screen("myEvents")
}