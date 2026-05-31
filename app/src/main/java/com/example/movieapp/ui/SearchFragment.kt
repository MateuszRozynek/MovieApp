package com.example.movieapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.viewmodel.SavedViewModel
import com.example.movieapp.viewmodel.SearchUiState
import com.example.movieapp.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val savedViewModel: SavedViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
        observeUiState()
        observeSavedStates()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onToggle = { movie, list -> savedViewModel.toggleMovie(movie, list) }
        )
        binding.recyclerView.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchViewModel.onQueryChanged(s?.toString() ?: "")
            }
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.uiState.collect { state ->
                binding.progressBar.isVisible = false
                binding.statusContainer.isVisible = false
                binding.recyclerView.isVisible = false

                when (state) {
                    is SearchUiState.Idle -> {
                        binding.statusIcon.setImageResource(R.drawable.ic_search_large)
                        binding.statusText.text = "Wpisz tytuł filmu, żeby go wyszukać"
                        binding.statusContainer.isVisible = true
                    }
                    is SearchUiState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is SearchUiState.Success -> {
                        binding.recyclerView.isVisible = true
                        movieAdapter.submitList(state.movies)
                        savedViewModel.checkSavedStates(state.movies)
                    }
                    is SearchUiState.Empty -> {
                        binding.statusIcon.setImageResource(R.drawable.ic_empty_search)
                        binding.statusText.text = "Nie znaleziono żadnych filmów"
                        binding.statusContainer.isVisible = true
                    }
                    is SearchUiState.Error -> {
                        binding.statusIcon.setImageResource(R.drawable.ic_empty_search)
                        binding.statusText.text = "Błąd: ${state.message}"
                        binding.statusContainer.isVisible = true
                    }
                }
            }
        }
    }

    private fun observeSavedStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            savedViewModel.savedStates.collect { states ->
                movieAdapter.updateSavedStates(states)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
