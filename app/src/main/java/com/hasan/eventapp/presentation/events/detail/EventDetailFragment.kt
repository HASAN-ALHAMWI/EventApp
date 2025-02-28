package com.hasan.eventapp.presentation.events.detail

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hasan.eventapp.R
import com.hasan.eventapp.databinding.FragmentEventDetailBinding
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.utils.extensions.formatPrice
import com.hasan.eventapp.utils.extensions.showDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    // ===========================
    // Properties
    // ===========================
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()
    private val args: EventDetailFragmentArgs by navArgs()

    // ===========================
    // Lifecycle Methods
    // ===========================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
        loadEventData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================
    // Initialization Methods
    // ===========================
    private fun initializeUi() {
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> renderUiState(state) }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            buttonBack.setOnClickListener { navigateBack() }
            buttonBookNow.setOnClickListener { initiateBookingProcess() }
            buttonRetry.setOnClickListener { retryLoading() }
        }
    }

    private fun loadEventData() {
        viewModel.loadEventDetails(args.eventId)
    }

    // ===========================
    // UI State Rendering
    // ===========================
    private fun renderUiState(state: EventDetailUiState) {
        when (state) {
            is EventDetailUiState.Loading -> renderLoadingState()
            is EventDetailUiState.Success -> renderSuccessState(state.event)
            is EventDetailUiState.Error -> renderErrorState(state.message)
        }
    }

    private fun renderLoadingState() {
        with(binding) {
            progressBar.isVisible = true
            contentGroup.isVisible = false
            errorGroup.isVisible = false
        }
    }

    private fun renderSuccessState(event: EventDomain) {
        with(binding) {
            progressBar.isVisible = false
            contentGroup.isVisible = true
            errorGroup.isVisible = false

            displayEventDetails(event)
        }
    }

    private fun renderErrorState(errorMessage: String) {
        with(binding) {
            progressBar.isVisible = false
            contentGroup.isVisible = false
            errorGroup.isVisible = true
            textError.text = errorMessage
        }
    }

    private fun displayEventDetails(event: EventDomain) {
        with(binding) {
            textTitle.text = event.title
            textDate.text = getString(R.string.event_date_time, event.date, event.time)
            textLocation.text = event.location
            textDescription.text = event.description
            textPrice.text = event.price.formatPrice()

            loadEventImage(event.imageUrl)
        }
    }

    private fun loadEventImage(imageUrl: String) {
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.imageEvent)
    }

    // ===========================
    // Navigation Methods
    // ===========================
    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun retryLoading() {
        loadEventData()
    }

    // ===========================
    // Booking Process Methods
    // ===========================
    private fun initiateBookingProcess() {
        getCurrentEvent()?.let { event ->
            showBookingConfirmationDialog(event)
        }
    }

    private fun getCurrentEvent(): EventDomain? {
        return (viewModel.uiState.value as? EventDetailUiState.Success)?.event
    }

    private fun showBookingConfirmationDialog(event: EventDomain) {
        showDialog(
            title = getString(R.string.confirm_booking_title),
            message = getString(R.string.confirm_booking_message, event.title, event.price.formatPrice()),
            positiveButtonText = getString(R.string.proceed),
            negativeButtonText = getString(R.string.cancel),
            onPositiveClick = { proceedToPayment(event) }
        )
    }

    private fun proceedToPayment(event: EventDomain) {
        val paymentBottomSheet = createPaymentBottomSheet(event)
        paymentBottomSheet.show(childFragmentManager, PaymentBottomSheetFragment.TAG)
    }

    private fun createPaymentBottomSheet(event: EventDomain): PaymentBottomSheetFragment {
        return PaymentBottomSheetFragment().newInstance(
            event.id,
            event.title,
            event.price
        )
    }
}