package com.hasan.eventapp.presentation.auth.login

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hasan.eventapp.R
import com.hasan.eventapp.databinding.FragmentLoginBinding
import com.hasan.eventapp.utils.UiAnimationHelper
import com.hasan.eventapp.utils.extensions.hideKeyboard
import com.hasan.eventapp.utils.extensions.setErrorWithVisibility
import com.hasan.eventapp.utils.extensions.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    // ===========================
    // Properties
    // ===========================
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    // ===========================
    // Lifecycle Methods
    // ===========================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
        observeLoginState()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================
    // UI Initialization
    // ===========================
    private fun initializeUi() {
        setupAnimations()
        setupClickListeners()
        setupInputValidation()
    }

    private fun setupAnimations() {
        UiAnimationHelper.animateLoginScreen(binding)
    }

    // ===========================
    // Event Listeners
    // ===========================
    private fun setupClickListeners() {
        with(binding) {
            loginButton.setOnClickListener {
                hideKeyboard()
                clearInputFocus()
                attemptLogin()
            }

            registerButton.setOnClickListener {
                navigateToRegister()
            }
        }
    }

    private fun setupInputValidation() {
        with(binding) {
            emailInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) emailInputLayout.setErrorWithVisibility(null)
            }

            passwordInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) passwordInputLayout.setErrorWithVisibility(null)
            }
        }
    }

    // ===========================
    // State Management
    // ===========================
    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest { state ->
                    handleLoginState(state)
                }
            }
        }
    }

    private fun handleLoginState(state: LoginState) {
        when (state) {
            is LoginState.Success -> navigateToEventList()
            is LoginState.ValidationError -> {
                displayValidationErrors(state.emailError, state.passwordError)
                resetLoginState()
            }

            is LoginState.ApiError -> {
                displayApiError(state.message)
                resetLoginState()
            }

            is LoginState.Loading -> updateLoadingState(true)
            is LoginState.Initial -> updateLoadingState(false)
        }
    }

    // ===========================
    // UI Actions
    // ===========================
    private fun attemptLogin() {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        viewModel.login(email, password)
    }

    private fun clearInputFocus() {
        binding.emailInput.clearFocus()
        binding.passwordInput.clearFocus()
    }

    private fun displayValidationErrors(emailError: String?, passwordError: String?) {
        emailError?.let { binding.emailInputLayout.setErrorWithVisibility(it) }
        passwordError?.let { binding.passwordInputLayout.setErrorWithVisibility(it) }
    }

    private fun displayApiError(message: String) {
        binding.root.showErrorSnackbar(message)
    }

    private fun updateLoadingState(isLoading: Boolean) {
        with(binding) {
            loginButton.isEnabled = !isLoading
            loginButton.text = if (isLoading) "" else getString(R.string.login_button)
            loginProgressBar.isVisible = isLoading
        }
    }

    private fun resetLoginState() {
        viewModel.resetState()
    }

    // ===========================
    // Navigation
    // ===========================
    private fun navigateToEventList() {
        findNavController().navigate(R.id.action_loginFragment_to_eventListFragment)
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}