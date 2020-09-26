package com.kes.meetupscanapp

import android.os.Parcel
import android.os.Parcelable

data class User(
    val company: String?,
    val email: String?,
    val middle_name: String?,
    val name: String?,
    val phone: String?,
    val position: String?,
    val surname: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(company)
        parcel.writeString(email)
        parcel.writeString(middle_name)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(position)
        parcel.writeString(surname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}