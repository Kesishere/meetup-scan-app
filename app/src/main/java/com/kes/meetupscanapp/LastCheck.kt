package com.kes.meetupscanapp

import android.os.Parcel
import android.os.Parcelable

data class LastCheck(
    val timestamp: String?,
    val diviceid: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(timestamp)
        parcel.writeString(diviceid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LastCheck> {
        override fun createFromParcel(parcel: Parcel): LastCheck {
            return LastCheck(parcel)
        }

        override fun newArray(size: Int): Array<LastCheck?> {
            return arrayOfNulls(size)
        }
    }
}