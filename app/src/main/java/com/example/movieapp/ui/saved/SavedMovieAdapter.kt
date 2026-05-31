package com.example.movieapp.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.movieapp.R
import com.example.movieapp.databinding.BottomSheetMoveToListBinding
import com.example.movieapp.databinding.ItemSavedMovieBinding
import com.example.movieapp.db.MovieList
import com.example.movieapp.db.SavedMovie
import com.example.movieapp.repository.genreNames
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class SavedMovieAdapter(
    private val currentList: MovieList,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onRemoveClick: (SavedMovie) -> Unit,
    private val onToggleFromSheet: (SavedMovie, MovieList) -> Unit,
    private val getListsForMovie: suspend (Int) -> Set<MovieList>
) : ListAdapter<SavedMovie, SavedMovieAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemSavedMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: SavedMovie) {
            binding.titleText.text = movie.title
            binding.yearText.text = movie.releaseYear
            binding.genresText.text = movie.genreNames()
            binding.ratingText.text = "%.1f".format(movie.voteAverage)

            val posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
            binding.posterImage.load(posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_movie_placeholder)
                error(R.drawable.ic_movie_placeholder)
                transformations(RoundedCornersTransformation(12f))
            }

            binding.btnRemove.setOnClickListener { onRemoveClick(movie) }

            binding.btnMore.setOnClickListener {
                lifecycleScope.launch {
                    val allLists = getListsForMovie(movie.id)
                    showBottomSheet(movie, allLists)
                }
            }
        }

        private fun showBottomSheet(movie: SavedMovie, initialLists: Set<MovieList>) {
            val context = binding.root.context
            val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
            val sh = BottomSheetMoveToListBinding.inflate(LayoutInflater.from(context))

            sh.movieTitle.text = movie.title

            val activeLists = initialLists.toMutableSet()

            fun refresh() {
                sh.checkWatchlist.isVisible = MovieList.WATCHLIST in activeLists
                sh.checkWatched.isVisible   = MovieList.WATCHED   in activeLists
                sh.checkFavourite.isVisible = MovieList.FAVOURITE in activeLists
                sh.btnMoveWatchlist.alpha = if (MovieList.WATCHLIST in activeLists) 1f else 0.5f
                sh.btnMoveWatched.alpha   = if (MovieList.WATCHED   in activeLists) 1f else 0.5f
                sh.btnMoveFavourite.alpha = if (MovieList.FAVOURITE in activeLists) 1f else 0.5f
            }

            refresh()

            sh.btnMoveWatchlist.setOnClickListener {
                onToggleFromSheet(movie, MovieList.WATCHLIST)
                if (MovieList.WATCHLIST in activeLists) activeLists.remove(MovieList.WATCHLIST)
                else activeLists.add(MovieList.WATCHLIST)
                refresh()
            }
            sh.btnMoveWatched.setOnClickListener {
                onToggleFromSheet(movie, MovieList.WATCHED)
                if (MovieList.WATCHED in activeLists) activeLists.remove(MovieList.WATCHED)
                else activeLists.add(MovieList.WATCHED)
                refresh()
            }
            sh.btnMoveFavourite.setOnClickListener {
                onToggleFromSheet(movie, MovieList.FAVOURITE)
                if (MovieList.FAVOURITE in activeLists) activeLists.remove(MovieList.FAVOURITE)
                else activeLists.add(MovieList.FAVOURITE)
                refresh()
            }

            dialog.setContentView(sh.root)
            dialog.show()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SavedMovie>() {
        override fun areItemsTheSame(oldItem: SavedMovie, newItem: SavedMovie) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SavedMovie, newItem: SavedMovie) =
            oldItem == newItem
    }
}
