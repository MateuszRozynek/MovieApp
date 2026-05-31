package com.example.movieapp.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movieapp.databinding.FragmentSavedBinding
import com.example.movieapp.db.MovieList
import com.google.android.material.tabs.TabLayoutMediator

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = SavedPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Do obejrzenia"
                1 -> "Obejrzane"
                2 -> "Ulubione"
                else -> ""
            }
            tab.icon = when (position) {
                0 -> requireContext().getDrawable(com.example.movieapp.R.drawable.ic_watchlist)
                1 -> requireContext().getDrawable(com.example.movieapp.R.drawable.ic_watched)
                2 -> requireContext().getDrawable(com.example.movieapp.R.drawable.ic_favourite)
                else -> null
            }
        }.attach()

        binding.viewPager.currentItem = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SavedPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SavedListFragment.newInstance(MovieList.WATCHLIST)
        1 -> SavedListFragment.newInstance(MovieList.WATCHED)
        2 -> SavedListFragment.newInstance(MovieList.FAVOURITE)
        else -> SavedListFragment.newInstance(MovieList.WATCHLIST)
    }
}
