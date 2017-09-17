package ru.goodibunakov.iremember;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SplashTask splashTask = new SplashTask();
        splashTask.execute();
        Animation an2= AnimationUtils.loadAnimation(getActivity(),R.anim.splash_anim);
        container.startAnimation(an2);
        return inflater.inflate(R.layout.fragment_splash, container, false);

    }

    class SplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            getActivity().getFragmentManager().popBackStack();

            return null;
        }
    }
}
