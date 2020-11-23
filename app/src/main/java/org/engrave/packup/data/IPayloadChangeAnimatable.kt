package org.engrave.packup.data

interface IPayloadChangeAnimatable<T> : IContentComparable<T> {
    fun keyFieldsSameWith(other: T): Boolean
    fun animatableFieldsSameWith(other: T): Boolean
    override fun contentSameWith(other: T) =
        keyFieldsSameWith(other) && animatableFieldsSameWith(other)
}