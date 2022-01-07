package org.volunteered.libs.core.extension

inline fun String.whenNotEmpty(func: (String) -> Unit) {
    if (this.isNotEmpty()) {
        func(this)
    }
}
