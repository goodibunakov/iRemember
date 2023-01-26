package ru.goodibunakov.iremember.presentation.view.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.databinding.ItemTaskBinding
import ru.goodibunakov.iremember.databinding.SeparatorBinding
import ru.goodibunakov.iremember.presentation.OnItemClickListener
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.Item
import ru.goodibunakov.iremember.presentation.model.ModelSeparator
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.utils.DateUtils
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_DATE_FULL

abstract class TasksAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var items: MutableList<Item>? = null
    private var lastPosition = -1


    var containsSeparatorOverdue: Boolean = false
    var containsSeparatorToday: Boolean = false
    var containsSeparatorTomorrow: Boolean = false
    var containsSeparatorFuture: Boolean = false

    init {
        items = ArrayList()
    }

    fun getItem(position: Int): Item {
        return items!![position]
    }

    abstract fun addTask(newTask: ModelTask)

    fun addItem(item: Item) {
        items!!.add(item)
        notifyItemInserted(itemCount - 1)
    }

    fun addItem(location: Int, item: Item) {
        items!!.add(location, item)
        notifyItemInserted(location)
    }

    fun removeItem(location: Int) {
        if (location >= 0 && location <= itemCount - 1) {
            items!!.removeAt(location)
            notifyItemRemoved(location)
            if (location - 1 >= 0 && location <= itemCount - 1) {
                if (!getItem(location).isTask() && !getItem(location - 1).isTask()) {
                    val separator = getItem(location - 1) as ModelSeparator
                    checkSeparator(separator.getType())
                    items!!.removeAt(location - 1)
                    notifyItemRemoved(location - 1)
                }
            } else if (itemCount - 1 >= 0 && !getItem(itemCount - 1).isTask()) {
                val separator = getItem(itemCount - 1) as ModelSeparator
                checkSeparator(separator.getType())

                val locationTemp = itemCount - 1
                items!!.removeAt(locationTemp)
                notifyItemRemoved(locationTemp)
            }
        }
    }

    private fun checkSeparator(type: Int) {
        when (type) {
            ModelSeparator.TYPE_OVERDUE -> containsSeparatorOverdue = false
            ModelSeparator.TYPE_TODAY -> containsSeparatorToday = false
            ModelSeparator.TYPE_TOMORROW -> containsSeparatorTomorrow = false
            ModelSeparator.TYPE_FUTURE -> containsSeparatorFuture = false
        }
    }

    override fun getItemCount(): Int {
        return if (items != null) items!!.size else 0
    }

    protected fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    fun removeAllItems() {
        if (itemCount != 0) {
            items!!.clear()
            notifyDataSetChanged()
            containsSeparatorFuture = false
            containsSeparatorTomorrow = false
            containsSeparatorToday = false
            containsSeparatorOverdue = false
        }
    }

    inner class TaskViewHolderCurrent(
        private val binding: ItemTaskBinding,
        private val onClickListener: OnItemClickListener,
        private val onLongClickListener: OnItemLongClickListener,
        private val onPriorityClickListener: OnPriorityClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            val model = item as ModelTask

            binding.root.isEnabled = true
            binding.root.visibility = View.VISIBLE
            binding.priority.isEnabled = true

            binding.tvTitle.text = model.title
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    binding.tvTitle.context,
                    android.R.color.black
                )
            )
            binding.tvDate.setTextColor(
                ContextCompat.getColor(
                    binding.tvDate.context,
                    android.R.color.darker_gray
                )
            )
            binding.priority.setImageResource(R.drawable.circle_full)
            binding.priority.setColorFilter(
                ContextCompat.getColor(
                    binding.priority.context,
                    model.getPriorityColor()
                )
            )

            binding.root.setOnClickListener { onClickListener.onItemClick(model) }
            binding.root.setOnLongClickListener { onLongClickListener.onItemLongClick(layoutPosition) }

            if (model.date != 0L) {
                binding.tvDate.visibility = View.VISIBLE
                binding.line.visibility = View.VISIBLE
                binding.tvDate.text = DateUtils.getDate(model.date, FORMAT_DATE_FULL)
            } else {
                binding.tvDate.visibility = View.GONE
                binding.line.visibility = View.GONE
            }


            binding.priority.setOnClickListener {
                binding.priority.isEnabled = false
                model.status = ModelTask.STATUS_DONE

                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        binding.tvTitle.context,
                        R.color.colorGray50
                    )
                )
                binding.tvDate.setTextColor(
                    ContextCompat.getColor(
                        binding.tvDate.context,
                        android.R.color.darker_gray
                    )
                )
                binding.priority.setColorFilter(
                    ContextCompat.getColor(
                        binding.priority.context,
                        model.getPriorityColor()
                    )
                )

                val flipIn = ObjectAnimator.ofFloat(binding.priority, View.ROTATION_Y, -180f, 0f)
                flipIn.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        if (model.status == ModelTask.STATUS_DONE) {
                            binding.priority.setImageResource(R.drawable.circle_checked)

                            val trans = ObjectAnimator.ofFloat(
                                binding.root,
                                View.TRANSLATION_X,
                                0f,
                                binding.root.width.toFloat()
                            )
                            val transBack = ObjectAnimator.ofFloat(
                                binding.root,
                                View.TRANSLATION_X,
                                binding.root.width.toFloat(),
                                0f
                            )

                            trans.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {}

                                override fun onAnimationEnd(animation: Animator) {
                                    binding.root.visibility = View.GONE
                                    removeItem(layoutPosition)
                                    onPriorityClickListener.onPriorityClick(model)
                                }

                                override fun onAnimationCancel(animation: Animator) {}

                                override fun onAnimationRepeat(animation: Animator) {}
                            })

                            val animatorSet = AnimatorSet()
                            animatorSet.play(trans).before(transBack)
                            animatorSet.start()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })

                flipIn.start()
            }
        }
    }


    inner class TaskViewHolderDone(
        private val binding: ItemTaskBinding,
        private val onLongClickListener: OnItemLongClickListener,
        private val onPriorityClickListener: OnPriorityClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.root.isEnabled = true
            binding.root.visibility = View.VISIBLE
            binding.priority.isEnabled = true

            val model = item as ModelTask
            binding.tvTitle.text = model.title
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    binding.tvTitle.context,
                    android.R.color.darker_gray
                )
            )
            binding.priority.setImageResource(R.drawable.circle_checked)
            binding.priority.setColorFilter(
                ContextCompat.getColor(
                    binding.priority.context,
                    model.getPriorityColor()
                )
            )

            binding.root.setOnLongClickListener { onLongClickListener.onItemLongClick(layoutPosition) }

            if (model.date != 0L) {
                binding.tvDate.visibility = View.VISIBLE
                binding.line.visibility = View.VISIBLE
                binding.tvDate.text = DateUtils.getDate(model.date, FORMAT_DATE_FULL)
            } else {
                binding.tvDate.visibility = View.GONE
                binding.line.visibility = View.GONE
            }


            binding.priority.setOnClickListener {
                binding.priority.isEnabled = false
                model.status = ModelTask.STATUS_CURRENT

                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        binding.tvTitle.context,
                        android.R.color.black
                    )
                )
                binding.tvDate.setTextColor(
                    ContextCompat.getColor(
                        binding.tvDate.context,
                        android.R.color.darker_gray
                    )
                )
                binding.priority.setColorFilter(
                    ContextCompat.getColor(
                        binding.priority.context,
                        model.getPriorityColor()
                    )
                )

                val flipIn = ObjectAnimator.ofFloat(binding.priority, View.ROTATION_Y, 180f, 0f)
                binding.priority.setImageResource(R.drawable.circle_full)
                flipIn.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        if (model.status != ModelTask.STATUS_DONE) {

                            val trans = ObjectAnimator.ofFloat(
                                binding.root,
                                View.TRANSLATION_X,
                                0f,
                                -binding.root.width.toFloat()
                            )
                            val transBack = ObjectAnimator.ofFloat(
                                binding.root,
                                View.TRANSLATION_X,
                                -binding.root.width.toFloat(),
                                0f
                            )

                            trans.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {}

                                override fun onAnimationEnd(animation: Animator) {
                                    binding.root.visibility = View.GONE
                                    removeItem(layoutPosition)
                                    onPriorityClickListener.onPriorityClick(model)
                                }

                                override fun onAnimationCancel(animation: Animator) {}

                                override fun onAnimationRepeat(animation: Animator) {}
                            })

                            val animatorSet = AnimatorSet()
                            animatorSet.play(trans).before(transBack)
                            animatorSet.start()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })

                flipIn.start()
            }
        }
    }

    inner class SeparatorViewHolder(binding: SeparatorBinding, val type: TextView) :
        RecyclerView.ViewHolder(binding.root)
}