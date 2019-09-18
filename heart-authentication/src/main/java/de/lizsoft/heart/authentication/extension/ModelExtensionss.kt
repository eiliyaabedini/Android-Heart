package de.lizsoft.heart.authentication.extension

import de.lizsoft.heart.interfaces.authentication.model.FirebaseUserModel
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.convert() : FirebaseUserModel = FirebaseUserModel(
      id = uid,
      displayName = displayName,
      email = email,
      avatarUrl = photoUrl,
      phoneNumber = phoneNumber
)