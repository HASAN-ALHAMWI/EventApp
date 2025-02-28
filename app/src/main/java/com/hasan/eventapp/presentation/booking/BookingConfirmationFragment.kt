package com.hasan.eventapp.presentation.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hasan.eventapp.R
import com.hasan.eventapp.databinding.FragmentBookingConfirmationBinding
import com.hasan.eventapp.utils.extensions.formatPrice
import com.hasan.eventapp.utils.extensions.formatToDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingConfirmationFragment : Fragment() {

    // ===========================
    // Properties
    // ===========================
    private var _binding: FragmentBookingConfirmationBinding? = null
    private val binding get() = _binding!!

    private val args: BookingConfirmationFragmentArgs by navArgs()

    // ===========================
    // Lifecycle Methods
    // ===========================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================
    // UI Setup Methods
    // ===========================
    private fun setupUI() {
        with(binding) {
            textEventTitle.text = args.eventTitle
            textBookingReference.text = args.bookingReference
            textTransactionId.text = args.transactionId
            textAmount.text = args.amount.formatPrice()

            textBookingDate.text = args.bookingDate.formatToDate()
        }
    }

    private fun setupListeners() {
        binding.buttonBackToEvents.setOnClickListener {
           navigateToEventList()
        }
    }

    // ===========================
    // Navigation Methods
    // ===========================
    private fun navigateToEventList() {
        findNavController().navigate(R.id.action_bookingConfirmationFragment_to_eventListFragment)
    }
}