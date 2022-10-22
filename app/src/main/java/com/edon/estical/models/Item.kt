package com.edon.estical.models

import com.edon.estical.R

data class Item(
    val id: Int,
    val title: String,
    val unitPrice: Float = 0f,
    val description: String = "",
    val image: Int = R.drawable.fl_studio_logo,
    val projectId: Int,
    val quantity: Int = 1
)
