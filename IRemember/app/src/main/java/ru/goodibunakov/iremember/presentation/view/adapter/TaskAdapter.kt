package ru.goodibunakov.iremember.presentation.view.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.model.Item
import ru.goodibunakov.iremember.presentation.model.ModelSeparator
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragment
import ru.goodibunakov.iremember.utils.Utils

abstract class TaskAdapter(val taskFragment: TaskFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    fun updateTask(newTask: ModelTask) {
        for (i in 0 until itemCount) {
            if (getItem(i).isTask()) {
                val task = getItem(i) as ModelTask
                if (newTask.timestamp == task.timestamp) {
                    removeItem(i)
                    taskFragment.addTask(newTask, false)
                }
            }
        }
    }

    protected fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(taskFragment.context, android.R.anim.slide_in_left)
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

    inner class TaskViewHolderCurrent(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Item) {
            val model = item as ModelTask

            itemView.isEnabled = true
            itemView.visibility = View.VISIBLE
            itemView.priority.isEnabled = true

            itemView.tvTitle.text = model.title
            itemView.tvTitle.setTextColor(ContextCompat.getColor(itemView.tvTitle.context, android.R.color.black))
            itemView.priority.setImageResource(R.drawable.circle_full)
            itemView.priority.setColorFilter(ContextCompat.getColor(itemView.priority.context, model.getPriorityColor()))

            itemView.setOnClickListener { taskFragment.showTaskEditDialog(model) }

            itemView.setOnLongClickListener {
                val handler = Handler()
                handler.postDelayed({ taskFragment.removeTaskDialog(layoutPosition) }, 1000)
                true
            }

            if (model.date != 0L) {
                itemView.tvDate.visibility = View.VISIBLE
                itemView.line.visibility = View.VISIBLE
                itemView.tvDate.text = Utils.getFullDate(model.date)
            } else {
                itemView.tvDate.visibility = View.GONE
                itemView.line.visibility = View.GONE
            }


            itemView.priority.setOnClickListener {
                itemView.priority.isEnabled = false
                model.status = ModelTask.STATUS_DONE
                taskFragment.activity!!.dbHelper!!.update().status(model.timestamp, ModelTask.STATUS_DONE)

                itemView.tvTitle.setTextColor(ContextCompat.getColor(itemView.tvTitle.context, R.color.gray50))
                itemView.tvDate.setTextColor(ContextCompat.getColor(itemView.tvDate.context, R.color.colorDarkGrey))
                itemView.priority.setColorFilter(ContextCompat.getColor(itemView.priority.context, model.getPriorityColor()))

                val flipIn = ObjectAnimator.ofFloat(itemView.priority, View.ROTATION_Y, -180f, 0f)
                flipIn.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if (model.status == ModelTask.STATUS_DONE) {
                            itemView.priority.setImageResource(R.drawable.circle_checked)

                            val trans = ObjectAnimator.ofFloat(itemView, View.TRANSLATION_X, 0f, itemView.width.toFloat())
                            val transBack = ObjectAnimator.ofFloat(itemView, View.TRANSLATION_X, itemView.width.toFloat(), 0f)

                            trans.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {

                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    itemView.visibility = View.GONE
                                    taskFragment.moveTask(model)
                                    removeItem(layoutPosition)
                                }

                                override fun onAnimationCancel(animation: Animator) {

                                }

                                override fun onAnimationRepeat(animation: Animator) {

                                }
                            })

                            val animatorSet = AnimatorSet()
                            animatorSet.play(trans).before(transBack)
                            animatorSet.start()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })

                flipIn.start()
            }
        }
    }


    inner class TaskViewHolderDone(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Item) {
            itemView.isEnabled = true
            itemView.visibility = View.VISIBLE
            itemView.priority.isEnabled = true

            val model = item as ModelTask
            itemView.tvTitle.text = model.title
            itemView.tvTitle.setTextColor(ContextCompat.getColor(itemView.tvTitle.context, android.R.color.darker_gray))
            itemView.priority.setImageResource(R.drawable.circle_checked)
            itemView.priority.setColorFilter(ContextCompat.getColor(itemView.priority.context, model.getPriorityColor()))

            itemView.setOnLongClickListener {
                val handler = Handler()
                handler.postDelayed({ taskFragment.removeTaskDialog(layoutPosition) }, 1000)
                true
            }

            if (model.date != 0L) {
                itemView.tvDate.visibility = View.VISIBLE
                itemView.line.visibility = View.VISIBLE
                itemView.tvDate.text = Utils.getFullDate(model.date)
            } else {
                itemView.tvDate.visibility = View.GONE
                itemView.line.visibility = View.GONE
            }


            itemView.priority.setOnClickListener {
                itemView.priority.isEnabled = false
                model.status = ModelTask.STATUS_CURRENT
                taskFragment.activity!!.dbHelper!!.update().status(model.timestamp, ModelTask.STATUS_CURRENT)

                itemView.tvTitle.setTextColor(ContextCompat.getColor(itemView.tvTitle.context, android.R.color.black))
                itemView.tvDate.setTextColor(ContextCompat.getColor(itemView.tvDate.context, android.R.color.darker_gray))
                itemView.priority.setColorFilter(ContextCompat.getColor(itemView.priority.context, model.getPriorityColor()))

                val flipIn = ObjectAnimator.ofFloat(itemView.priority, View.ROTATION_Y, 180f, 0f)
                itemView.priority.setImageResource(R.drawable.circle_full)
                flipIn.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if (model.status != ModelTask.STATUS_DONE) {

                            val trans = ObjectAnimator.ofFloat(itemView, View.TRANSLATION_X, 0f, -itemView.width.toFloat())
                            val transBack = ObjectAnimator.ofFloat(itemView, View.TRANSLATION_X, -itemView.width.toFloat(), 0f)

                            trans.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {

                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    itemView.visibility = View.GONE
                                    taskFragment.moveTask(model)
                                    removeItem(layoutPosition)
                                }

                                override fun onAnimationCancel(animation: Animator) {

                                }

                                override fun onAnimationRepeat(animation: Animator) {

                                }
                            })

                            val animatorSet = AnimatorSet()
                            animatorSet.play(trans).before(transBack)
                            animatorSet.start()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })

                flipIn.start()
            }
        }
    }

    inner class SeparatorViewHolder(itemView: View, val type: TextView) : RecyclerView.ViewHolder(itemView)
}