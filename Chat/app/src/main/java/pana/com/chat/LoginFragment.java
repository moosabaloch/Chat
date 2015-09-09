package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    private Button loginButton, createAccountButton;
    private EditText emailToLogin, passwordToLogin;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        loginButton = (Button) view.findViewById(R.id.buttonLoginFragmentLogin);
        createAccountButton = (Button) view.findViewById(R.id.buttonLoginFragmentCreateAccount);
        emailToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentEmailAddress);
        passwordToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentPassword);
        createNewAccount();
        loginaction();
        return view;
    }

    private void loginaction() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void createNewAccount() {
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
