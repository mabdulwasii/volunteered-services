package org.volunteered.libs.core.extension

inline fun Int.whenGreaterThanZero(func: (Int) -> Unit) {
    if (this > 0) {
        func(this)
    }
}