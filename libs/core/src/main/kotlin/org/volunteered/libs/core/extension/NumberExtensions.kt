package org.volunteered.libs.core.extension

inline fun Int.whenGreaterThanZero(func: (Int) -> Unit) {
    if (this > 0) {
        func(this)
    }
}

inline fun Long.whenGreaterThanZero(func: (Long) -> Unit) {
    if (this > 0) {
        func(this)
    }
}
