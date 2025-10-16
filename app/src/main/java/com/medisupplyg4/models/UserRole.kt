package com.medisupplyg4.models

import androidx.annotation.StringRes
import com.medisupplyg4.R

enum class UserRole(
    @param:StringRes val titleResId: Int
) {
    DRIVER(titleResId = R.string.role_driver),
    CLIENT(titleResId = R.string.role_client),
    SELLER(titleResId = R.string.role_seller)
}
