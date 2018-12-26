package dta4316.testapp.ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dta4316.testapp.Common.Authentication;
import dta4316.testapp.R;

public class AboutMeFragment extends Fragment {
    private static final String TAG = "AboutMeFragment";
    private OnAboutMeFragmentSignedOutListener m_SignedOutListener;

    @BindView(R.id.linkSignOut) TextView linkSignOut;

    public AboutMeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);
        ButterKnife.bind(this, view);

        linkSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOut();
            }
        });

        return view;
    }

    private void SignOut(){
        Authentication.SignOut();

        if (m_SignedOutListener != null) {
            m_SignedOutListener.OnAboutMeFragmentSignedOut();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAboutMeFragmentSignedOutListener) {
            m_SignedOutListener = (OnAboutMeFragmentSignedOutListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        m_SignedOutListener = null;
    }

    public interface OnAboutMeFragmentSignedOutListener {
        void OnAboutMeFragmentSignedOut();
    }
}
