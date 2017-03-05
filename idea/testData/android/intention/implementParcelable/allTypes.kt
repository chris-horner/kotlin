// INTENTION_CLASS: org.jetbrains.kotlin.android.intention.ImplementParcelableAction

import android.os.Parcel
import android.os.Parcelable

open class BaseParcelable(parcel: Parcel) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseParcelable> {
        override fun createFromParcel(parcel: Parcel): BaseParcelable {
            return BaseParcelable(parcel)
        }

        override fun newArray(size: Int): Array<BaseParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

class <caret>MyData {

    val delegated by lazy { 0 }
    val readonlyField = true

    var field1: Int = 0
    var field2: String = ""
    var field3: Long = 0
    var field4: Byte = 0
    var field5: Char = '\u0000'
    var field6: Short = 0
    var field7: Float = 0.0f
    var field8: Double = 0.0
    var field9: Boolean = false

    var goodArray: Array<BaseParcelable> = emptyArray()
    var badArray: Array<Parcelable> = emptyArray()

    var goodList: List<BaseParcelable> = emptyList()
    var badList: List<Parcelable> = emptyList()

    var parcelableProperty: SuperParcelable
    val uninitializedVal: Int

    val fieldWIthGetter: Int
        get() {
            return 0
        }

    var fieldWithCustomGetterAndSetter: Int = 0
        get() {
            return field
        }
        set(value: Int) {
            field = value
        }
}