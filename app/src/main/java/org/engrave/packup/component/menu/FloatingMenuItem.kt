package org.engrave.packup.component.menu

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes

class FloatingMenuItem @JvmOverloads constructor(
    id: Int,
    title: String,
    @DrawableRes
    iconResourceId: Int? = null,
    isChecked: Boolean = false,
    showDividerBelow: Boolean = false
) : Parcelable {

    interface OnClickListener {
        fun onFloatingMenuItemClicked(floatingMenuItem: FloatingMenuItem)
    }

    val id: Int
    val title: String

    @DrawableRes
    val iconResourceId: Int?
    var isChecked: Boolean
    val showDividerBelow: Boolean

    init {
        this.id = id
        this.title = title
        this.iconResourceId = iconResourceId
        this.isChecked = isChecked
        this.showDividerBelow = showDividerBelow
    }

    private constructor(parcel: Parcel) :
            this(
                id = parcel.readInt(),
                title = parcel.readString() ?: "",
                iconResourceId = parcel.readInt(),
                isChecked = parcel.readByte() != 0.toByte(),
                showDividerBelow = parcel.readByte() != 0.toByte()
            )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeValue(iconResourceId)
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeByte(if (showDividerBelow) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FloatingMenuItem> {
        override fun createFromParcel(parcel: Parcel): FloatingMenuItem = FloatingMenuItem(parcel)
        override fun newArray(size: Int): Array<FloatingMenuItem?> = arrayOfNulls(size)
    }

}