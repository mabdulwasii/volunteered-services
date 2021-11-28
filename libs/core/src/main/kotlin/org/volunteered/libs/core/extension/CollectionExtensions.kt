package org.volunteered.libs.core.extension

inline fun <E: Any, T: Collection<E>> T.whenNotEmpty(func: (T) -> Unit) {
    if (this.isNotEmpty()) {
        func(this)
    }
}
