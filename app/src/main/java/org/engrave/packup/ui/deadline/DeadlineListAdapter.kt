package org.engrave.packup.ui.deadline

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.engrave.packup.R
import org.engrave.packup.component.DistinctiveDiffCallback
import org.engrave.packup.component.IDistinctive
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.databinding.ItemDeadlineHeaderBinding
import org.engrave.packup.databinding.ItemDeadlineMemberBinding
import org.engrave.packup.util.asLocalCalendar
import org.engrave.packup.util.toGlobalizedString
import ws.vinta.pangu.Pangu

sealed class DeadlineItem : IDistinctive

data class DeadlineMember(val deadline: Deadline) : DeadlineItem() {
    override fun getTypeDescriptor() = "M"

    override fun getIdentityDescriptor() = deadline.uid.toString()

    override fun getModifierDescriptor() = ""

    override fun isOfSameContent(other: IDistinctive): Boolean =
        if (other is DeadlineMember) this.deadline.contentSameWith(other.deadline)
        else false
}

data class DeadlineHeader(val title: String, val num: Int) : DeadlineItem() {
    override fun getTypeDescriptor() = "H"

    override fun getIdentityDescriptor() = title

    override fun getModifierDescriptor() = ""

    override fun isOfSameContent(other: IDistinctive): Boolean =
        if (other is DeadlineHeader) this.title == other.title && this.num == other.num
        else false
}

/*class DeadlineItemDiffCallback : DiffUtil.ItemCallback<DeadlineItem>() {
    override fun areItemsTheSame(oldItem: DeadlineItem, newItem: DeadlineItem): Boolean =
        when {
            oldItem is DeadlineMember && newItem is DeadlineMember && oldItem.deadline.uid == newItem.deadline.uid -> true
            oldItem is DeadlineHeader && newItem is DeadlineHeader && oldItem.title == oldItem.title -> true
            else -> false
        }

    override fun areContentsTheSame(oldItem: DeadlineItem, newItem: DeadlineItem): Boolean =
        if (oldItem is DeadlineMember && newItem is DeadlineMember) {
            with(oldItem.deadline) {
                newItem.deadline.let { n ->
                    name == n.name
                            && source_name == n.source_name
                            && due_time == n.due_time
                            && is_deleted == n.is_deleted
                            && is_finished == n.is_finished
                            && is_starred == n.is_starred
                            && has_submission == n.has_submission
                }
            }
        } else if (oldItem is DeadlineHeader && newItem is DeadlineHeader) {
            oldItem.details == oldItem.details
        } else false
}*/


class DeadlineListAdapter(
    private val context: Context,
    private val onClickStar: (Int, Boolean) -> Unit
) :
    ListAdapter<DeadlineItem, RecyclerView.ViewHolder>(DistinctiveDiffCallback<DeadlineItem>()) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)
    val pangu = Pangu()
    val haveSubmissionString = context.getString(R.string.have_submission)
    val noSubmissionString = context.getString(R.string.no_submission)
    val haveSubmissionDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_packup_submission_status_24_filled)

    val noSubmissionDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_packup_submission_status_24_regular)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            DEADLINE_ITEM_HEADER -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeadlineHeaderBinding.inflate(layoutInflater, parent, false)
                DeadlineHeaderViewHolder(binding)
            }
            DEADLINE_ITEM_MEMBER -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeadlineMemberBinding.inflate(layoutInflater, parent, false)
                DeadlineMemberViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType when inflating deadline list.")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is DeadlineHeaderViewHolder -> holder.bind(getItem(position) as DeadlineHeader)
        is DeadlineMemberViewHolder -> holder.bind(getItem(position) as DeadlineMember) { bool ->
            Log.e("position", position.toString())
            onClickStar((getItem(position) as DeadlineMember).deadline.uid, bool)
        }
        else -> throw ClassCastException("Unknown ViewHolder Class ${holder::class.simpleName} when inflating deadline list.")
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is DeadlineMember -> DEADLINE_ITEM_MEMBER
        is DeadlineHeader -> DEADLINE_ITEM_HEADER
    }

    interface DeadlineItemViewHolder

    inner class DeadlineMemberViewHolder internal constructor(private val binding: ItemDeadlineMemberBinding) :
        RecyclerView.ViewHolder(binding.root), DeadlineItemViewHolder {
        fun bind(item: DeadlineMember, onClickStarBind: (Boolean)->Unit) {
            binding.apply {
                deadlineItemMemberTitle.text = pangu.spacingText(item.deadline.name)
                deadlineItemMemberDueTime.text =
                    item.deadline.due_time.asLocalCalendar()?.toGlobalizedString(context)
                deadlineItemMemberSubmissionButton.isChecked = item.deadline.has_submission
                deadlineItemMemberStarButton.apply {
                    setOnCheckedChangeListener { _, isChecked ->
                        onClickStarBind(isChecked)
                    }
                    isChecked = item.deadline.is_starred
                }
                deadlineItemMemberCourseText.text =
                    pangu.spacingText(item.deadline.source_course_name)
            }
        }
    }

    inner class DeadlineHeaderViewHolder internal constructor(private val binding: ItemDeadlineHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), DeadlineItemViewHolder {
        fun bind(header: DeadlineHeader) {
            binding.apply {
                deadlineItemHeaderDigest.text = header.title
                deadlineItemHeaderDetail.text = header.num.toString()
            }
        }
    }

    companion object {
        const val DEADLINE_ITEM_HEADER = 0
        const val DEADLINE_ITEM_MEMBER = 1
    }
}
