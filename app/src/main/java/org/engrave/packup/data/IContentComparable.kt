package org.engrave.packup.data

interface IContentComparable<T> {
    fun contentSameWith(other: T): Boolean
}