package com.example.bmob

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
        binding.bottomNavigationView.setupWithNavController(navController)

        setBottomNavigationView()
    }
    private fun setBottomNavigationView(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (
                destination.id == R.id.loginFragment
                || destination.id == R.id.registerFragment
                || destination.id == R.id.verifyFragment
                || destination.id == R.id.startFragment
                || destination.id == R.id.usernameFragment
                || destination.id == R.id.phoneNumberFragment
                || destination.id == R.id.resetPasswordFragment
            ) {
                binding.bottomNavigationView.visibility = View.GONE
            }else{
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 当用户处于顶层页面(工作台，首页，我的)时，
     * 点击返回键返回桌面
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
            if (
                navController.currentDestination?.id == R.id.homeFragment
            ) {
                gotoDesktop(this)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 退出到桌面  后台运行
     * @param context
     */
    private fun gotoDesktop(context: Context) {
        //后台运行 退出到桌面
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(intent)
    }
}