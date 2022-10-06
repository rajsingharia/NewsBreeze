package com.example.newsapp

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.util.NetworkConnectivity
import com.example.newsapp.view.fragment.SavedFragmentDirections
import com.example.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val newsViewModel: NewsViewModel by viewModels()
    lateinit var networkConnectivity : NetworkConnectivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
        val navController = navHostFragment.navController

        binding.savedArticle.setOnClickListener {
            val action = SavedFragmentDirections.actionGlobalSavedFragment()
            navController.navigate(action)
        }

        networkConnectivity = NetworkConnectivity(application)

        observeConnectivity()

        getNews()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.splashFragment -> {
                    stateSplash()
                }
                R.id.homeFragment-> {
                    stateHome()
                }
                R.id.detailFragment -> {
                    stateDetail()
                }
                R.id.savedFragment -> {
                    stateSave()
                }
            }
        }
    }

    private fun observeConnectivity() {
        networkConnectivity.observe(this) {
            it?.let { online ->
                if (!online) {
                    binding.offlineTextView.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.slide_down
                        )
                    )
                    binding.offlineTextView.visibility = View.VISIBLE
                } else {
                    binding.offlineTextView.visibility = View.GONE
                    newsViewModel.getBreakingNews()
                }

            }
        }
    }

    private fun getNews() {
        newsViewModel.getBreakingNews()
    }

    private fun stateSave() {
        binding.homeAppbar.visibility = View.GONE
    }

    private fun stateDetail() {
        binding.homeAppbar.visibility = View.GONE
    }

    private fun stateHome() {
        binding.homeAppbar.visibility = View.VISIBLE
    }

    private fun stateSplash() {
        binding.homeAppbar.visibility = View.GONE
    }
}