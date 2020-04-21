package com.saloYD.ciphermessage.Classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val userUid : String, val username : String, val  userImage : String) : Parcelable {

    constructor() : this("","","")

}