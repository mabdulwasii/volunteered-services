package org.volunteered.libs.util

import java.util.Locale

class IsoUtil {
    companion object {
        private val ISO_COUNTRIES = Locale.getISOCountries()

        fun isValidISOCountry(s: String?): Boolean {
            return ISO_COUNTRIES.contains(s)
        }
    }
}
