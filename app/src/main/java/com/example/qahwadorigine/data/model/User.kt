package com.example.qahwadorigine.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val email: String,
    val name: String,
    val password: String? = null // Exemple de nullable - mot de passe optionnel
)