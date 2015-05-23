package io.ringle.rudy

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

data
public open class RouteResolution
(
        val uri: String = "",
        val key: String = "",
        val params: Bundle = Bundle()
) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uri)
        dest.writeString(key)
        dest.writeBundle(params)
    }

    companion object {

        val CREATOR = object : Parcelable.Creator<RouteResolution> {

            override fun createFromParcel(source: Parcel): RouteResolution? =
                    RouteResolution(
                            source.readString(),
                            source.readString(),
                            source.readBundle()
                    )

            override fun newArray(size: Int): Array<RouteResolution?>? {
                return arrayOfNulls(size)
            }
        }
    }
}
