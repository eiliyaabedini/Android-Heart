package de.lizsoft.heart.interfaces.authentication.model

import android.net.Uri
import java.io.Serializable

data class FirebaseUserModel(
      val id: String,
      val displayName: String?,
      val email: String?,
      val avatarUrl: Uri?,
      val phoneNumber: String?
) : Serializable