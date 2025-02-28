package com.hasan.eventapp.presentation.payment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hasan.eventapp.R
import com.hasan.eventapp.databinding.FragmentPaymentBottomSheetBinding
import com.hasan.eventapp.presentation.events.detail.EventDetailFragmentDirections
import com.hasan.eventapp.utils.extensions.formatCardNumber
import com.hasan.eventapp.utils.extensions.formatExpiryDate
import com.hasan.eventapp.utils.extensions.formatPrice
import com.hasan.eventapp.utils.extensions.hideKeyboard
import com.hasan.eventapp.utils.extensions.setErrorWithVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentBottomSheetFragment : BottomSheetDialogFragment() {

    // ===========================
    // Properties
    // ===========================
    private var _binding: FragmentPaymentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by viewModels()
    private val args: PaymentBottomSheetFragmentArgs by navArgs()

    companion object {
        const val TAG = "PaymentBottomSheet"
    }

    // ===========================
    // Lifecycle Methods
    // ===========================
    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        _binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================
    // Initialization
    // ===========================
    private fun initializeUI() {
        setupEventDetails()
        setupInputFormatters()
        setupClickListeners()
        setupInputValidation()
        observeStateFlows()
    }

    private fun setupEventDetails() {
        binding.textEventTitle.text = args.eventTitle
        binding.textPrice.text = args.eventPrice.formatPrice()
    }

    private fun setupInputFormatters() {
        // Setup input formatting helpers
        binding.editCardNumber.formatCardNumber()
        binding.editExpiryDate.formatExpiryDate()
    }

    private fun setupClickListeners() {
        binding.buttonClose.setOnClickListener { dismiss() }
        binding.buttonPayNow.setOnClickListener {
            hideKeyboard()
            clearInputFocus()
            submitPayment()
        }
    }

    private fun setupInputValidation() {
        with(binding) {
            editCardNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) inputLayoutCardNumber.setErrorWithVisibility(null)
            }
            editExpiryDate.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) inputLayoutExpiryDate.setErrorWithVisibility(null)
            }
            editCvv.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) inputLayoutCvv.setErrorWithVisibility(null)
            }
            editCardholderName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) inputLayoutCardholderName.setErrorWithVisibility(null)
            }
        }
    }

    // ===========================
    // UI Actions
    // ===========================
    private fun submitPayment() {
        viewModel.processPayment(
            eventId = args.eventId,
            cardNumber = binding.editCardNumber.text.toString(),
            expiryDate = binding.editExpiryDate.text.toString(),
            cvv = binding.editCvv.text.toString(),
            cardholderName = binding.editCardholderName.text.toString(),
            amount = args.eventPrice
        )
    }

    private fun clearInputFocus() {
        binding.editCardNumber.clearFocus()
        binding.editExpiryDate.clearFocus()
        binding.editCvv.clearFocus()
        binding.editCardholderName.clearFocus()
    }

    // ===========================
    // State Management
    // ===========================
    private fun observeStateFlows() {
        observePaymentState()
        observeBookingState()
    }

    private fun observePaymentState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paymentState.collectLatest { state ->
                    handlePaymentState(state)
                }
            }
        }
    }

    private fun observeBookingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createBookingState.collectLatest { state ->
                    handleBookingState(state)
                }
            }
        }
    }

    // ===========================
    // State Handling
    // ===========================
    private fun handlePaymentState(state: PaymentState) {
        when (state) {
            is PaymentState.Initial -> resetUI()
            is PaymentState.Processing -> showProcessingUI()
            is PaymentState.ApiError -> {
                displayApiError(state.message)
            }

            is PaymentState.ValidationError -> {
                displayValidationErrors(
                    state.cardNumberError,
                    state.expiryDateError,
                    state.cvvError,
                    state.cardholderNameError
                )
                resetPaymentState()
            }
        }
    }

    private fun handleBookingState(state: CreateBookingState?) {
        when (state) {
            is CreateBookingState.Success -> {
                binding.progressIndicator.visibility = View.GONE
                navigateToBookingConfirmation(
                    eventTitle = args.eventTitle,
                    bookingReference = state.booking.bookingReference,
                    transactionId = state.booking.paymentId,
                    amount = args.eventPrice,
                    bookingDate = state.booking.bookingDate
                )
            }

            is CreateBookingState.Error -> {
                binding.buttonPayNow.isEnabled = true
                binding.progressIndicator.visibility = View.GONE
                binding.textError.visibility = View.VISIBLE
                binding.textError.text = state.message
            }

            else -> { /* No-op for other states */
            }
        }
    }

    // ===========================
    // UI State Updates
    // ===========================
    private fun resetUI() {
        binding.buttonPayNow.isEnabled = true
        binding.progressIndicator.visibility = View.GONE
        binding.textError.visibility = View.GONE
    }

    private fun showProcessingUI() {
        binding.buttonPayNow.isEnabled = false
        binding.progressIndicator.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE
    }

    private fun displayValidationErrors(
        cardNumberError: String?,
        expiryDateError: String?,
        cvvError: String?,
        cardholderNameError: String?
    ) {
        binding.inputLayoutCardholderName.setErrorWithVisibility(cardholderNameError)
        binding.inputLayoutCardNumber.setErrorWithVisibility(cardNumberError)
        binding.inputLayoutExpiryDate.setErrorWithVisibility(expiryDateError)
        binding.inputLayoutCvv.setErrorWithVisibility(cvvError)
    }

    private fun displayApiError(message: String) {
        binding.buttonPayNow.isEnabled = true
        binding.progressIndicator.visibility = View.GONE
        binding.textError.visibility = View.VISIBLE
        binding.textError.text = message
    }

    private fun resetPaymentState() {
        viewModel.resetState()
    }

    // ===========================
    // Navigation
    // ===========================
    private fun navigateToBookingConfirmation(
        eventTitle: String,
        bookingReference: String,
        transactionId: String,
        amount: Float,
        bookingDate: Long
    ) {
        val action = EventDetailFragmentDirections.actionPaymentBottomSheetToBookingConfirmation(
            eventTitle = eventTitle,
            bookingReference = bookingReference,
            transactionId = transactionId,
            amount = amount,
            bookingDate = bookingDate
        )
        findNavController().navigate(action)
        dismiss()
    }

    // ===========================
    // Factory Methods
    // ===========================
    fun newInstance(
        eventId: String,
        eventTitle: String,
        eventPrice: Float
    ): PaymentBottomSheetFragment {
        return PaymentBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString("eventId", eventId)
                putString("eventTitle", eventTitle)
                putFloat("eventPrice", eventPrice)
            }
        }
    }
}