package com.kes.meetupscanapp

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    val user: User?,
    val valid: Boolean,
    val lastCheck: LastCheck?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(LastCheck::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeByte(if (valid) 1 else 0)
        parcel.writeParcelable(lastCheck, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}