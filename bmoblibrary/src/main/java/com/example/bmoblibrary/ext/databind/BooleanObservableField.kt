package com.example.bmoblibrary.ext.databind

import androidx.databinding.ObservableField

class BooleanObservableField(value: Boolean = true) : ObservableField<Boolean>(value) {
    override fun get(): Boolean? {
        return super.get()!!
    }
}