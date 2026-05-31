package com.example.movieapp.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentSavedListBinding
import com.example.movieapp.db.MovieList
import com.example.movieapp.viewmodel.SavedViewModel
import kotlinx.coroutines.launch

class SavedListFragment : Fragment() {

    companion object {
        private const val ARG_LIST = "arg_list"
        fun newInstance(list: MovieList) = SavedListFragment().apply {
            arguments = Bundle().apply { putString(ARG_LIST, list.name) }
        }
    }

    private var _binding: FragmentSavedListBinding? = null
    private val binding get() = _binding!!

    private val savedViewModel: SavedViewModel by activityViewModels()
    private lateinit var adapter: SavedMovieAdapter
    private lateinit var movieList: MovieList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieList = MovieList.valueOf(requireArguments().getString(ARG_LIST)!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SavedMovieAdapter(
            currentList = movieList,
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            onRemoveClick = { movie ->
                savedViewModel.removeFromList(movie.id, movieList)
            },
            onToggleFromSheet = { movie, targetList ->
                savedViewModel.toggleFromSheet(movie, targetList)
            },
            getListsForMovie = { movieId ->
                savedViewModel.getListsForMovieSuspend(movieId)
            }
        )

        binding.recyclerView.apply {
            this.adapter = this@SavedListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            savedViewModel.getMoviesByList(movieList).collect { movies ->
                if (movies.isEmpty()) {
                    binding.recyclerView.isVisible = false
                    binding.emptyContainer.isVisible = true
                    when (movieList) {
                        MovieList.WATCHLIST -> {
                            binding.emptyIcon.setImageResource(R.drawable.ic_watchlist)
                            binding.emptyText.text = "Brak filmów do obejrzenia\nDodaj filmy ze wyszukiwarki"
                        }
                        MovieList.WATCHED -> {
                            binding.emptyIcon.setImageResource(R.drawable.ic_watched)
                            binding.emptyText.text = "Nie masz jeszcze obejrzanych filmów"
                        }
                        MovieList.FAVOURITE -> {
                            binding.emptyIcon.setImageResource(R.drawable.ic_favourite)
                            binding.emptyText.text = "Brak ulubionych filmów"
                        }
                    }
                } else {
                    binding.recyclerView.isVisible = true
                    binding.emptyContainer.isVisible = false
                    adapter.submitList(movies)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
