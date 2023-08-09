package com.example.locationtest

class Nordschleife {

    companion object {
        private data class Waypoint(
            val name: String,
            val latitude1: Double,
            val longitude1: Double,
            val latitude2: Double,
            val longitude2: Double
        )

        data class WpData(
            val waypointName: String,
            val waypointIndex: Int
        )

        private val rounds = listOf<String>(
            "Setzrunde", "Sprint", "Sprint", "Sprint",
            "Sprint", "Sprint", "InLap", "OutLap", "Bestätigungsrunde", "Sprint", "Sprint",
            "Sprint", "Sprint", "Sprint", "InLap MaxZeit"
        )
        private val waypoints = listOf(
            Waypoint("Start/Ziel", 50.33754, 6.95043, 50.33859, 6.95012),
            Waypoint("Hocheichen", 50.33963, 6.93437, 50.34188, 6.93346),
            Waypoint("Flugplatz", 50.34483, 6.92614, 50.34621, 6.9266),
            Waypoint("Schwedenkreuz", 50.35467, 6.92385, 50.35661, 6.92434),
            Waypoint("Fuchsröhre", 50.35851, 6.92295, 50.35993, 6.9234),
            Waypoint("Adenauer Forst", 50.36478, 6.93089, 50.36675, 6.93074),
            Waypoint("Metzgesfeld", 50.37203, 6.93694, 50.37054, 6.93844),
            Waypoint("Kallenhard", 50.37341, 6.93443, 50.37502, 6.93355),
            Waypoint("Miss-Hit-Miss", 50.37696, 6.93943, 50.37835, 6.93754),
            Waypoint("Wehrseifen", 50.37557, 6.94368, 50.3774, 6.94203),
            Waypoint("Ex-Mühle", 50.37743, 6.95124, 50.37882, 6.94963),
            Waypoint("Bergwerk", 50.38023, 6.96107, 50.38135, 6.95933),
            Waypoint("Kesselchen", 50.37423, 6.96566, 50.37519, 6.96821),
            Waypoint("Mutkurve", 50.37197, 6.97843, 50.37318, 6.9815),
            Waypoint("Steilstrecke", 50.37405, 6.98955, 50.37493, 6.98783),
            Waypoint("Hohe Acht", 50.37491, 6.99453, 50.37636, 6.99339),
            Waypoint("Wippermann", 50.37498, 6.99966, 50.37551, 7.00279),
            Waypoint("Eschbach", 50.37148, 7.00402, 50.37275, 7.00236),
            Waypoint("Brünnchen", 50.36831, 7.00503, 50.36914, 7.00354),
            Waypoint("Pflanzgarten 1", 50.36483, 7.0003, 50.36593, 6.99904),
            Waypoint("Pflanzgarten 2", 50.36039, 6.99534, 50.36219, 6.99485),
            Waypoint("Schwalbenschwanz", 50.35754, 6.98719, 50.35888, 6.98539),
            Waypoint("Galgenkopf", 50.35553, 6.98405, 50.35651, 6.98644),
            Waypoint("Döttinger Höhe", 50.35196, 6.98227, 50.35193, 6.98026),
            Waypoint("Touristenzufahrt", 50.34632, 6.96729, 50.34625, 6.96525),
            Waypoint("Antoniusbuche", 50.34361, 6.96072, 50.34316, 6.95825),
            Waypoint("Hohenrain", 50.33842, 6.95379, 50.33801, 6.95261)
        )

        fun isWaypointReached(latitude: Double, longitude: Double): WpData {
            for ((index, waypoint) in waypoints.withIndex()) {
                if (latitude >= minOf(waypoint.latitude1, waypoint.latitude2)
                    && latitude <= maxOf(waypoint.latitude1, waypoint.latitude2)
                    && longitude >= minOf(waypoint.longitude1, waypoint.longitude2)
                    && longitude <= maxOf(waypoint.longitude1, waypoint.longitude2)
                )
                    return WpData(waypoint.name, index)
            }
            return WpData("", -1)
        }

        fun getRoundName(roundNo: Int): String {
            return rounds[roundNo - 1]
        }

        fun getSectorCount(): Int {
            return waypoints.count()
        }
    }
}