package com.taskapp.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.taskapp.R
import com.taskapp.common.viewBinding
import com.taskapp.databinding.FragmentHomePageBinding

class HomePageFragment : Fragment(R.layout.fragment_home_page) {

    private val binding by viewBinding(FragmentHomePageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeFragment =
            childFragmentManager.findFragmentById(R.id.nav_home_fragment) as? NavHostFragment
        val navController = homeFragment?.navController

        if (navController != null) {
            binding.bottomNavigationView.setupWithNavController(navController)
        }
    }
}