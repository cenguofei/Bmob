package com.example.bmob

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import cn.bmob.v3.Bmob
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_PROVOST
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.ActivityMainBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmoblibrary.base.baseactivity.BaseVbActivity

class MainActivity : BaseVbActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController


    override fun initView(savedInstanceState: Bundle?) {
        binding.root.invalidate()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun createObserver() {
        /**
         * 设置一个侦听器，当选择导航项时，该侦听器将收到通知。
         * 当重新选择当前选定的项目时，此侦听器也会收到通知，
         * 除非出现NavigationBarView
         */
        appViewModel.userIdentification.observe(this) {
            with(binding) {
                when (it) {
                    IDENTIFICATION_STUDENT -> {
                        bottomDeanNavigationView.visibility = View.GONE
                        bottomTeacherNavigationView.visibility = View.GONE
                        bottomProvostNavigationView.visibility = View.GONE
                        bottomStudentNavigationView.visibility = View.VISIBLE
                        bottomStudentNavigationView.setupWithNavController(navController)
                    }
                    IDENTIFICATION_TEACHER -> {
                        bottomDeanNavigationView.visibility = View.GONE
                        bottomStudentNavigationView.visibility = View.GONE
                        bottomProvostNavigationView.visibility = View.GONE
                        bottomTeacherNavigationView.visibility = View.VISIBLE
                        bottomTeacherNavigationView.setupWithNavController(navController)
                    }
                    IDENTIFICATION_DEAN -> {
                        bottomTeacherNavigationView.visibility = View.GONE
                        bottomStudentNavigationView.visibility = View.GONE
                        bottomProvostNavigationView.visibility = View.GONE
                        bottomDeanNavigationView.visibility = View.VISIBLE
                        bottomDeanNavigationView.setupWithNavController(navController)
                    }
                    IDENTIFICATION_PROVOST -> {
                        bottomDeanNavigationView.visibility = View.GONE
                        bottomStudentNavigationView.visibility = View.GONE
                        bottomTeacherNavigationView.visibility = View.GONE
                        bottomProvostNavigationView.visibility = View.VISIBLE
                        bottomProvostNavigationView.setupWithNavController(navController)
                    }
                }
            }
        }
    }

    override fun setEventListener() {
        setBottomNavigationView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Bmob.initialize(this, "6c64658a44bd4a2260c527c3ca385248")
        super.onCreate(savedInstanceState)
    }

    private fun setBottomNavigationView() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (
                destination.id == R.id.loginFragment
                || destination.id == R.id.registerFragment
                || destination.id == R.id.verifyFragment
                || destination.id == R.id.startFragment
                || destination.id == R.id.usernameFragment
                || destination.id == R.id.phoneNumberFragment
                || destination.id == R.id.resetPasswordFragment
                || destination.id == R.id.searchFragment
                || destination.id == R.id.browseFragment
                || destination.id == R.id.selectFragment
                || destination.id == R.id.studentThesisFragment
                || destination.id == R.id.showThesisFragment
                || destination.id == R.id.setFragment
                || destination.id == R.id.teacherReleasedFragment
                || destination.id == R.id.teacherSelectResultFragment
                || destination.id == R.id.teacherNewThesisFragment
                || destination.id == R.id.approveFragment
                || destination.id == R.id.deanApprovedFragment
                || destination.id == R.id.deanNotApprovedFragment
                || destination.id == R.id.studentSelectedFragment
                || destination.id == R.id.studentUnselectedFragment
                || destination.id == R.id.provostSelectTimeFragment
                || destination.id == R.id.skimFragment
            ) {
                when (appViewModel.userIdentification.value) {
                    IDENTIFICATION_STUDENT -> {
                        binding.bottomStudentNavigationView.visibility = View.GONE
                    }
                    IDENTIFICATION_TEACHER -> {
                        binding.bottomTeacherNavigationView.visibility = View.GONE
                    }
                    IDENTIFICATION_DEAN -> {
                        binding.bottomDeanNavigationView.visibility = View.GONE
                    }
                    IDENTIFICATION_PROVOST -> {
                        binding.bottomProvostNavigationView.visibility = View.GONE
                    }
                }
            } else {
                when (appViewModel.userIdentification.value) {
                    IDENTIFICATION_STUDENT -> {
                        binding.bottomStudentNavigationView.visibility = View.VISIBLE
                    }
                    IDENTIFICATION_TEACHER -> {
                        binding.bottomTeacherNavigationView.visibility = View.VISIBLE
                    }
                    IDENTIFICATION_DEAN -> {
                        binding.bottomDeanNavigationView.visibility = View.VISIBLE
                    }
                    IDENTIFICATION_PROVOST -> {
                        binding.bottomProvostNavigationView.visibility = View.VISIBLE
                    }
                }
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
                || navController.currentDestination?.id == R.id.studentHomeFragment
                || navController.currentDestination?.id == R.id.mineFragment
                || navController.currentDestination?.id == R.id.provostHomeFragment
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