package org.engrave.packup.data

interface IPayloadChangeAnimatable<T> : IContentComparable<T> {
    fun keyFieldsSameWith(other: T): Boolean
    fun manipulatableFieldsSameWith(other: T): Boolean
    override fun contentSameWith(other: T) =
        keyFieldsSameWith(other) && manipulatableFieldsSameWith(other)
}