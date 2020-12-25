package org.engrave.packup.data

interface IContentComparable<T> {
    fun contentsSameWith(other: T): Boolean
}