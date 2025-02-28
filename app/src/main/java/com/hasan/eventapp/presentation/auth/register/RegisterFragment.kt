package com.hasan.eventapp.presentation.auth.register

import android.os.Bundle
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
import com.hasan.eventapp.databinding.FragmentRegisterBinding
import com.hasan.eventapp.utils.UiAnimationHelper
import com.hasan.eventapp.utils.extensions.hideKeyboard
import com.hasan.eventapp.utils.extensions.setErrorWithVisibility
import com.hasan.eventapp.utils.extensions.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    // ===========================
    // Properties
    // ===========================
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    // ===========================
    // Lifecycle Methods
    // ===========================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        observeRegistrationState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================
    // UI Initialization
    // ===========================
    private fun initializeUI() {
        setupInputListeners()
        setupButtonListeners()
        setupAnimations()
    }

    private fun setupAnimations() {
        UiAnimationHelper.animateRegisterScreen(binding)
    }

    // ===========================
    // Event Listeners
    // ===========================
    private fun setupButtonListeners() {
        binding.registerButton.setOnClickListener {
            clearAllFocus()
            hideKeyboard()
            performRegister()
        }

        binding.loginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun setupInputListeners() {
        // Map of input fields to their respective layouts
        val inputFieldLayouts = mapOf(
            binding.nameInput to binding.nameInputLayout,
            binding.emailInput to binding.emailInputLayout,
            binding.passwordInput to binding.passwordInputLayout,
            binding.confirmPasswordInput to binding.confirmPasswordInputLayout
        )

        // Apply focus change listener to each input field
        inputFieldLayouts.forEach { (input, layout) ->
            input.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) layout.setErrorWithVisibility(null)
            }
        }
    }

    // ===========================
    // State Management
    // ===========================
    private fun observeRegistrationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registrationState.collectLatest(::handleRegistrationState)
            }
        }
    }

    private fun handleRegistrationState(state: RegistrationState) {
        when (state) {
            is RegistrationState.Success -> navigateToEventList()
            is RegistrationState.ValidationError -> {
                displayValidationErrors(
                    state.nameError,
                    state.emailError,
                    state.passwordError,
                    state.confirmPasswordError
                )
                resetRegistrationState()
            }
            is RegistrationState.ApiError -> {
                displayApiError(state.message)
                resetRegistrationState()
            }
            is RegistrationState.Loading -> updateLoadingState(true)
            is RegistrationState.Initial -> updateLoadingState(false)
        }
    }


    // ===========================
    // UI Actions
    // ===========================
    private fun updateLoadingState(isLoading: Boolean) {
        with(binding) {
            registerButton.isEnabled = !isLoading
            registerButton.text = if (isLoading) "" else getString(R.string.register_button)
            loadingProgressBar.isVisible = isLoading

            // Clear error states when loading
            if (isLoading) {
                nameInputLayout.setErrorWithVisibility(null)
                emailInputLayout.setErrorWithVisibility(null)
                passwordInputLayout.setErrorWithVisibility(null)
                confirmPasswordInputLayout.setErrorWithVisibility(null)
            }
        }
    }

    private fun displayValidationErrors(
        nameError: String?,
        emailError: String?,
        passwordError: String?,
        confirmPasswordError: String?
    ) {
        with(binding) {
            nameError?.let { nameInputLayout.setErrorWithVisibility(it) }
            emailError?.let { emailInputLayout.setErrorWithVisibility(it) }
            passwordError?.let { passwordInputLayout.setErrorWithVisibility(it) }
            confirmPasswordError?.let { confirmPasswordInputLayout.setErrorWithVisibility(it) }
        }
    }

    private fun displayApiError(message: String) {
        binding.root.showErrorSnackbar(message)
    }

    private fun resetRegistrationState() {
        viewModel.resetState()
    }

    private fun performRegister() {
        viewModel.register(
            binding.nameInput.text.toString(),
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString(),
            binding.confirmPasswordInput.text.toString()
        )
    }

    private fun clearAllFocus() {
        with(binding) {
            nameInput.clearFocus()
            emailInput.clearFocus()
            passwordInput.clearFocus()
            confirmPasswordInput.clearFocus()
        }
    }

    // ===========================
    // Navigation
    // ===========================
    private fun navigateToEventList() {
        findNavController().navigate(R.id.action_registerFragment_to_eventListFragment)
    }

    private fun navigateToLogin() {
        findNavController().navigateUp()
    }
}