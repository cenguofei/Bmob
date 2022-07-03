package com.example.bmob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import cn.bmob.v3.Bmob
import com.example.bmob.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Bmob.initialize(this,"6c64658a44bd4a2260c527c3ca385248")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.visibility = View.GONE
        binding.root.invalidate()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as NavHostFragment
        navController = navHostFragment.navController
        setBottomNavigationView()
    }
    private fun setBottomNavigationView(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (
                destination.id == R.id.loginFragment
                || destination.id == R.id.registerFragment
                || destination.id == R.id.verifyFragment
            ) {
                binding.bottomNavigationView.visibility = View.GONE
            }else{
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}