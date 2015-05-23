package io.ringle.rudy

import android.os.Parcel
import android.os.Parcelable

public open class RouteRecord(
        val key: Int,
        val addToBackstack: Boolean,
        val resolution: RouteResolution
) : Parcelable {

    var routable: Routable? = null

    companion object {

        val CREATOR = object : Parcelable.Creator<RouteRecord> {

            val cl = javaClass<RouteRecord>().getClassLoader()

            override fun createFromParcel(source: Parcel): RouteRecord? =
                    RouteRecord(
                            source.readInt(),
                            source.readInt() == 1,
                            source.readParcelable(cl)
                    )

            override fun newArray(size: Int): Array<RouteRecord?>? {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(key)
        dest.writeInt(if (addToBackstack) 1 else 0)
        dest.writeParcelable(resolution, 0)
    }
}
