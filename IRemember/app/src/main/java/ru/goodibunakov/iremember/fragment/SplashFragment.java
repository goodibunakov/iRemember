package ru.goodibunakov.iremember.fragment;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import ru.goodibunakov.iremember.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment implements Animation.AnimationListener{

    Animation splash_in;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SplashTask splashTask = new SplashTask();
        splashTask.execute();

        splash_in = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_in);
        splash_in.setAnimationListener(this);
        container.startAnimation(splash_in);

        return inflater.inflate(R.layout.fragment_splash, container, false);
    }



    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == splash_in) {
            Animation splash_out = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_out);
            ImageView iv = (ImageView) getActivity().findViewById(R.id.logo);
            splash_out.setStartOffset(1000);
            iv.startAnimation(splash_out);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }



    class SplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (getActivity() != null) {
                getActivity().getFragmentManager().popBackStack();
            }
            return null;
        }
    }
}
