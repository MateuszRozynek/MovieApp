package com.example.movieapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.movieapp.R
import com.example.movieapp.databinding.ItemMovieBinding
import com.example.movieapp.db.MovieList
import com.example.movieapp.model.GENRE_MAP
import com.example.movieapp.model.Movie

class MovieAdapter(
    private val onToggle: (Movie, MovieList) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    private var savedStates: Map<Int, Set<MovieList>> = emptyMap()

    fun updateSavedStates(states: Map<Int, Set<MovieList>>) {
        savedStates = states
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie, savedStates[movie.id] ?: emptySet())
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, activeLists: Set<MovieList>) {
            binding.titleText.text = movie.title
            binding.yearText.text = movie.year

            val genres = movie.genreIds
                .mapNotNull { GENRE_MAP[it] }
                .take(3)
                .joinToString(" • ")
            binding.genresText.text = genres.ifEmpty { "Brak gatunków" }
            binding.ratingText.text = "%.1f".format(movie.voteAverage)

            binding.posterImage.load(movie.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_movie_placeholder)
                error(R.drawable.ic_movie_placeholder)
                transformations(RoundedCornersTransformation(12f))
            }

            binding.btnWatchlist.alpha  = if (MovieList.WATCHLIST  in activeLists) 1f else 0.3f
            binding.btnWatched.alpha    = if (MovieList.WATCHED    in activeLists) 1f else 0.3f
            binding.btnFavourite.alpha  = if (MovieList.FAVOURITE  in activeLists) 1f else 0.3f

            binding.btnWatchlist.setBackgroundResource(
                if (MovieList.WATCHLIST in activeLists) R.drawable.btn_icon_bg_active
                else R.drawable.btn_icon_bg
            )
            binding.btnWatched.setBackgroundResource(
                if (MovieList.WATCHED in activeLists) R.drawable.btn_icon_bg_active
                else R.drawable.btn_icon_bg
            )
            binding.btnFavourite.setBackgroundResource(
                if (MovieList.FAVOURITE in activeLists) R.drawable.btn_icon_bg_active
                else R.drawable.btn_icon_bg
            )

            binding.btnWatchlist.setOnClickListener { onToggle(movie, MovieList.WATCHLIST) }
            binding.btnWatched.setOnClickListener   { onToggle(movie, MovieList.WATCHED) }
            binding.btnFavourite.setOnClickListener { onToggle(movie, MovieList.FAVOURITE) }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}
