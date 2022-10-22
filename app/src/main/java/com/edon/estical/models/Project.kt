package com.edon.estical.models

import com.edon.estical.R

data class Project(
    val id: Int = 0,
    val image: Int = R.drawable.fl_studio_logo,
    val title: String,
    val estimation: Float = 0.0f,
    val description: String = "",
)
