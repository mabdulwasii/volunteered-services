package org.volunteered.apps.review.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class StringEncoder {
    companion object {
        fun hashValue(stringValue: String): String {
            val md = MessageDigest.getInstance("MD5")
            val hashInBytes = md.digest(
                stringValue.toByteArray(StandardCharsets.UTF_8)
            )
            val sb = StringBuilder()
            for (b in hashInBytes) {
                sb.append(String.format("%02x", b))
            }
            return sb.toString()
        }
    }
}