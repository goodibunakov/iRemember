package ru.goodibunakov.iremember.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.dialog.AddingTaskDialogFragment;


public class CurrentTaskFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    public CurrentTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_task, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.fab)
    void onClick() {
        DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
        if (getFragmentManager() != null) {
            addingTaskDialogFragment.show(getFragmentManager(), "AddingTaskDialogFragment");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
