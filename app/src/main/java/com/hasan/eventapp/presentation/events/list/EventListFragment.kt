package com.hasan.eventapp.presentation.events.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hasan.eventapp.R
import com.hasan.eventapp.databinding.FragmentEventListBinding
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.utils.extensions.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EventListFragment : Fragment() {

    // ===========================
    // Properties
    // ===========================
    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventListViewModel by viewModels()
    private val eventAdapter by lazy { EventAdapter(::navigateToDetails) }
    private val skeletonAdapter by lazy { EventSkeletonAdapter(SKELETON_ITEM_COUNT) }

    // ===========================
    // Lifecycle Methods
    // ===========================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        observeViewModelStates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================
    // UI Initialization
    // ===========================
    private fun initializeUI() {
        setupRecyclerViews()
        setupRefreshControls()
        setupLogoutListener()
    }

    private fun setupRecyclerViews() {
        binding.recyclerView.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.skeletonRecyclerView.apply {
            adapter = skeletonAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupRefreshControls() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshEvents()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.refreshEvents()
        }
    }

    private fun setupLogoutListener() {
        binding.customToolbar.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // ===========================
    // ViewModel Observation
    // ===========================
    private fun observeViewModelStates() {
        observeEventListState()
        observeLogoutState()
    }

    private fun observeEventListState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect(::handleEventState)
        }
    }

    private fun observeLogoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutState.collectLatest(::handleLogoutState)
            }
        }
    }

    // ===========================
    // State Handling
    // ===========================
    private fun handleEventState(state: EventListState) {
        binding.swipeRefreshLayout.isRefreshing = false

        when (state) {
            is EventListState.Success -> updateUIForSuccess(state.events)
            is EventListState.Error -> showError(state.message)
            is EventListState.Loading -> showLoadingIfNeeded()
        }
    }

    private fun handleLogoutState(state: LogoutState) {
        when (state) {
            is LogoutState.Success -> navigateToLogin()
            is LogoutState.Error -> binding.root.showErrorSnackbar(state.message)
            else -> { /* No action needed for other states */
            }
        }
    }

    // ===========================
    // UI State Management
    // ===========================
    private fun updateUIForSuccess(events: List<EventDomain>) {
        if (events.isEmpty()) {
            showEmptyState()
        } else {
            showEvents(events)
        }
    }

    private fun showEvents(events: List<EventDomain>) {
        showContent()
        eventAdapter.submitList(events.sortedByDescending { it.date })
    }

    private fun showEmptyState() {
        with(binding) {
            emptyStateContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            skeletonRecyclerView.visibility = View.GONE
            errorText.visibility = View.GONE
        }
    }

    private fun showLoadingIfNeeded() {
        if (eventAdapter.currentList.isEmpty()) {
            showLoading()
        }
    }

    private fun showContent() {
        binding.apply {
            skeletonRecyclerView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            emptyStateContainer.visibility = View.GONE
        }
    }

    private fun showLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            skeletonRecyclerView.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            emptyStateContainer.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        binding.apply {
            recyclerView.visibility = View.GONE
            skeletonRecyclerView.visibility = View.GONE
            errorText.apply {
                text = message
                visibility = View.VISIBLE
            }
            emptyStateContainer.visibility = View.GONE
        }
    }

    // ===========================
    // Navigation
    // ===========================
    private fun navigateToDetails(event: EventDomain) {
        findNavController().navigate(
            EventListFragmentDirections.actionEventListFragmentToEventDetailFragment(event.id)
        )
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_eventListFragment_to_loginFragment)
    }

    // ===========================
    // User Actions
    // ===========================
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> performLogout() }
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun performLogout() {
        viewModel.logout()
    }

    // ===========================
    // Constants
    // ===========================
    companion object {
        private const val SKELETON_ITEM_COUNT = 5
    }
}