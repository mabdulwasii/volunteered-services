package org.volunteered.libs.extension

inline fun String.whenNotEmpty(func: (String) -> Unit) {
    if (this.isNotEmpty()) {
        func(this)
    }
}