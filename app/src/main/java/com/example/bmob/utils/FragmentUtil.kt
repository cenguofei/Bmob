package com.example.bmob.utils

import androidx.fragment.app.Fragment
import com.example.bmob.data.storage.SettingsDataStore

fun Fragment.getSettingsDataStore(): SettingsDataStore {
    context?.let {
        return SettingsDataStore.getInstance(it)
    }
    throw IllegalStateException("Fragment $this not attached to a context.")
}