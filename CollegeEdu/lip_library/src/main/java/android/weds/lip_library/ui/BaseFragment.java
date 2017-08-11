package android.weds.lip_library.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.weds.lip_library.App;



/**
 * Created by lip on 2016/9/19.
 *
 * 底层fragment
 */
public class BaseFragment extends Fragment implements View.OnClickListener{
    protected Context context;
    public View rootView;
    protected App myApp;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
        myApp = (App) getActivity().getApplication();
    }

    public View getRootView(){
        return rootView;
    }
    @Override
    public void onClick(View v) {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
