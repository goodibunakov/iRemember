package ru.goodibunakov.iremember.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.Utils;
import ru.goodibunakov.iremember.fragment.TaskFragment;
import ru.goodibunakov.iremember.model.Item;
import ru.goodibunakov.iremember.model.ModelTask;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Item> items;
    private TaskFragment taskFragment;
    private int lastPosition = -1;

    TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
        items = new ArrayList<>();
        Log.d("debug", "TaskAdapter создан! " + this);
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addItem(int location, Item item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void removeItem(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            items.remove(location);
            notifyItemRemoved(location);
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    class TaskViewHolderCurrent extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        TextView date;
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.priority)
        AppCompatImageView priority;
        @BindView(R.id.card_view)
        CardView cardView;

        TaskViewHolderCurrent(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Item item) {
            ModelTask model = (ModelTask) item;
            title.setText(model.getTitle());
            title.setTextColor(ContextCompat.getColor(title.getContext(), android.R.color.black));
            priority.setImageResource(R.drawable.circle_full);
            priority.setColorFilter(ContextCompat.getColor(priority.getContext(), model.getPriorityColor()));

            itemView.setOnLongClickListener(v -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> getTaskFragment().removeTaskDialog(getLayoutPosition()), 1000);
                return true;
            });

            if (model.getDate() != 0) {
                date.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                date.setText(Utils.getFullDate(model.getDate()));
            } else {
                date.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }


            priority.setOnClickListener(v -> {
                model.setStatus(ModelTask.STATUS_DONE);
                getTaskFragment().activity.dbHelper.update().status(model.getTimestamp(), ModelTask.STATUS_DONE);

                title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.gray50));
                date.setTextColor(ContextCompat.getColor(date.getContext(), R.color.colorDarkGrey));
                priority.setColorFilter(ContextCompat.getColor(priority.getContext(), model.getPriorityColor()));

                ObjectAnimator flipIn = ObjectAnimator.ofFloat(priority, "rotationY", -180F, 0F);
                flipIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (model.getStatus() == ModelTask.STATUS_DONE) {
                            priority.setImageResource(R.drawable.circle_checked);

                            ObjectAnimator trans = ObjectAnimator.ofFloat(itemView, "translationX", 0F, itemView.getWidth());
                            ObjectAnimator transBack = ObjectAnimator.ofFloat(itemView, "translationX", itemView.getWidth(), 0F);

                            trans.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itemView.setVisibility(View.GONE);
                                    getTaskFragment().moveTask(model);
                                    removeItem(getLayoutPosition());
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(trans).before(transBack);
                            animatorSet.start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                flipIn.start();
            });
        }
    }

    class TaskViewHolderDone extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        TextView date;
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.priority)
        AppCompatImageView priority;
        @BindView(R.id.card_view)
        CardView cardView;

        TaskViewHolderDone(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Item item) {
            ModelTask model = (ModelTask) item;
            title.setText(model.getTitle());
            title.setTextColor(ContextCompat.getColor(title.getContext(), android.R.color.darker_gray));
            priority.setImageResource(R.drawable.circle_checked);
            priority.setColorFilter(ContextCompat.getColor(priority.getContext(), model.getPriorityColor()));

            itemView.setOnLongClickListener(v -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> getTaskFragment().removeTaskDialog(getLayoutPosition()), 1000);
                return true;
            });

            if (model.getDate() != 0) {
                date.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                date.setText(Utils.getFullDate(model.getDate()));
            } else {
                date.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }


            priority.setOnClickListener(v -> {
                model.setStatus(ModelTask.STATUS_CURRENT);
                getTaskFragment().activity.dbHelper.update().status(model.getTimestamp(), ModelTask.STATUS_CURRENT);

                title.setTextColor(ContextCompat.getColor(title.getContext(), android.R.color.black));
                date.setTextColor(ContextCompat.getColor(date.getContext(), android.R.color.darker_gray));
                priority.setColorFilter(ContextCompat.getColor(priority.getContext(), model.getPriorityColor()));

                ObjectAnimator flipIn = ObjectAnimator.ofFloat(priority, "rotationY", 180F, 0F);
                priority.setImageResource(R.drawable.circle_full);
                flipIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (model.getStatus() != ModelTask.STATUS_DONE) {

                            ObjectAnimator trans = ObjectAnimator.ofFloat(itemView, "translationX", 0F, -itemView.getWidth());
                            ObjectAnimator transBack = ObjectAnimator.ofFloat(itemView, "translationX", -itemView.getWidth(), 0F);

                            trans.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itemView.setVisibility(View.GONE);
                                    getTaskFragment().moveTask(model);
                                    removeItem(getLayoutPosition());
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(trans).before(transBack);
                            animatorSet.start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                flipIn.start();
            });
        }
    }

    private TaskFragment getTaskFragment() {
        return taskFragment;
    }

    void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(taskFragment.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}