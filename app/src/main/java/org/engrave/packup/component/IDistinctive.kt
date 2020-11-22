package org.engrave.packup.component

import androidx.recyclerview.widget.DiffUtil
import org.engrave.packup.data.IContentComparable
import org.engrave.packup.ui.deadline.DeadlineItem

// TODO: 所有要 DiffUtil 的Item全部继承 IDistinctive
interface IDistinctive {
    fun getTypeDescriptor(): String
    fun getIdentityDescriptor(): String
    fun getModifierDescriptor(): String
    fun isOfSameContent(other: IDistinctive): Boolean
}

class DistinctiveDiffCallback<T: IDistinctive> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        (oldItem.getTypeDescriptor() == newItem.getTypeDescriptor())
                && (oldItem.getIdentityDescriptor() == newItem.getIdentityDescriptor())

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.isOfSameContent(newItem)
}