package ru.goodibunakov.iremember.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.goodibunakov.iremember.R;


public class SplashFragment extends Fragment implements Animation.AnimationListener {

    private Animation splash_in;
    private Animation splash_out;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new SplashTask().execute();

        splash_in = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_in);
        splash_in.setAnimationListener(this);
        container.startAnimation(splash_in);
        splash_out = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_out);
        splash_out.setAnimationListener(this);
        splash_out.setStartOffset(1000);

        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == splash_in) {
            Objects.requireNonNull(getView()).startAnimation(splash_out);
        }
        if (animation == splash_out) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(SplashFragment.this)
                        .commit();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    private static class SplashTask extends AsyncTask<Void, Void, Void> {

        SplashTask() {
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}