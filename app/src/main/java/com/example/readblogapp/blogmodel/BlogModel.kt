package com.example.readblogapp.blogmodel

import android.os.Parcel
import android.os.Parcelable

 data class BlogModel(
    var heading: String? ="null",
    val userName: String? ="null",
    val date: String? ="null",
    var post: String? ="null",
    val profile_image: String?= null,
    val userId: String? =null,
    var likeCount: Int = 0,
    var isSaved :Boolean= false,
    var postId: String?= "null",
    var likedBy: MutableList<String>?=null

): Parcelable{
         constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte()!=0.toByte(),
        parcel.readString()
    ) {}

     override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeString(profile_image)
        parcel.writeString(userId)
        parcel.writeInt(likeCount)
        parcel.writeByte(if(isSaved)1 else 0)
        parcel.writeString(postId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogModel> {
        override fun createFromParcel(parcel: Parcel): BlogModel {
            return BlogModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogModel?> {
            return arrayOfNulls(size)
        }
    }

 }


