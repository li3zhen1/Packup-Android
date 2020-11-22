package org.engrave.packup.data

interface IContentComparable<T> {
    fun isOfSameContent(other: T): Boolean
}