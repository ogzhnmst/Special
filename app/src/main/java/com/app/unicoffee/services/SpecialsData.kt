package com.app.unicoffee.services

import java.sql.Timestamp

data class SpecialsData(

    val specialName: String,
    val specialDetails: String,
    val locationlink: String,
    val imagelink: String,
    val date: com.google.firebase.Timestamp,
    val specialPrice: String,
    val specialLocationName: String,

    )