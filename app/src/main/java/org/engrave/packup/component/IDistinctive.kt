package org.engrave.packup.component

import androidx.recyclerview.widget.DiffUtil
import org.engrave.packup.ui.deadline.DeadlineItem

// TODO: 所有要 DiffUtil 的Item全部继承 IDistinctive
interface IDistinctive {
    fun getTypeDescriptor(): String
    fun getIdentityDescriptor(): String
    fun getContentDescriptor(): String
    fun getModifierDescriptor(): String
}

class DistinctiveDiffCallback<T : IDistinctive> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        (oldItem.getTypeDescriptor() == newItem.getTypeDescriptor())
                && (oldItem.getIdentityDescriptor() == newItem.getIdentityDescriptor())

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.getContentDescriptor() == newItem.getContentDescriptor()
}