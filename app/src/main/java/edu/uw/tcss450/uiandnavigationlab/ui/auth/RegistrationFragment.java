package edu.uw.tcss450.uiandnavigationlab.ui.auth;

import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator.checkPwdUpperCase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.uiandnavigationlab.databinding.FragmentRegistrationBinding;
import edu.uw.tcss450.uiandnavigationlab.utils.PasswordValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    private RegistrationViewModel mRegisterModel;

    private PasswordValidator mNameValidator = checkPwdLength(1);

    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd ->
                    pwd.equals(binding.registrationEditTextPasswordConfirm.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegistrationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registrationButtonSubmit.setOnClickListener(button->{
            attemptRegister(button);
            binding.layoutWait.setVisibility(View.VISIBLE);
        });
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                response->{
                    observeResponse(response);
                    binding.layoutWait.setVisibility(View.GONE);
                });
    }

    private void attemptRegister(final View button) {
        validateFirst();
    }

    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.registrationEditTextFirstName.getText().toString().trim()),
                this::validateLast,
                result -> binding.registrationEditTextFirstName.setError("Please enter a first name."));
    }

    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.registrationEditTextLastName.getText().toString().trim()),
                this::validateEmail,
                result -> binding.registrationEditTextLastName.setError("Please enter a last name."));
    }

    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.registrationEditTextEmail.getText().toString().trim()),
                this::validatePasswordsMatch,
                result -> binding.registrationEditTextEmail.setError("Please enter a valid Email address."));
    }

    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(binding.registrationEditTextPasswordConfirm.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(binding.registrationEditTextPassword.getText().toString().trim()),
                this::validatePassword,
                result -> binding.registrationEditTextPassword.setError("Passwords must match."));
    }

    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.registrationEditTextPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.registrationEditTextPassword.setError("Please enter a valid Password."));
    }

    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                binding.registrationEditTextFirstName.getText().toString(),
                binding.registrationEditTextLastName.getText().toString(),
                binding.registrationEditTextEmail.getText().toString(),
                binding.registrationEditTextPassword.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    private void navigateToLogin() {
        RegistrationFragmentDirections.ActionRegistrationFragmentToSignInFragment directions =
                RegistrationFragmentDirections.actionRegistrationFragmentToSignInFragment();

        directions.setEmail(binding.registrationEditTextEmail.getText().toString());
        directions.setPassword(binding.registrationEditTextPassword.getText().toString());

        Navigation.findNavController(getView()).navigate(directions);

    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.registrationEditTextEmail.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}