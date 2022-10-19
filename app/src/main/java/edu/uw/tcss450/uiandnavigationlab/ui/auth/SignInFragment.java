package edu.uw.tcss450.uiandnavigationlab.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import edu.uw.tcss450.uiandnavigationlab.R;
import edu.uw.tcss450.uiandnavigationlab.databinding.FragmentSignInBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentSignInBinding binding = FragmentSignInBinding.bind(requireView());

        //On button click, navigate to MainActivity
        binding.buttonSignin.setOnClickListener(button -> {
            Navigation.findNavController(requireView()).navigate(
                    SignInFragmentDirections
                            .actionSignInFragmentToMainActivity(
                                    generateJwt(binding.editEmail.getText().toString())
                            ));
            //This tells the containing Activity that we are done with it.
            //It will not be added to backstack.
            getActivity().finish();
        });


    }

    /**
     * This helper method is creating a JSON Web Token (JWT). In future labs, the JWT will
     * be created and sent to us from the Web Service. For now, we will "fake" that and create
     * the JWT client-side. This is ANTI-PATTERN!!! Do not create JWTs client-side.
     *
     * @param email the email used to encode into the JWT
     * @return the resulting JWT
     */
    private String generateJwt(final String email) {
        String token;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret key don't use a string literal in " +
                    "production code!!!");
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("email", email)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("JWT Failed to Create.");
        }
        return token;
    }
}
