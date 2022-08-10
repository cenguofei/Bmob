package com.example.bmob.fragments.mine.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.databinding.FragmentSetBinding
import com.example.bmob.databinding.SexPopupWindowBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.getSettingsDataStore
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort

/**
 * 设置用户信息界面
 *
 * not permitted by network
 * 显然是一个系统限制访问的问题
 * 在高版本安卓不允许访问非https域名的接口
 *
 * CLEARTEXT communication to bmob-cdn-30807.bmobpay.com not permitted by network security policy
 *
 * 下载文件的时候发下出现上面异常，然后就找到了解决上传图片成功，
 * 但是显示不了方法：
 * android:usesCleartextTraffic="true"
 */
class SetFragment : BaseVbFragment<FragmentSetBinding>() {
    private val viewModel: SetViewModel by activityViewModels()
    private val args: SetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setRegister(this)
        Log.v(LOG_TAG, "SetFragment  viewModel $viewModel")
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.user = args.userInfo
        binding.click = ProxyClick()
    }

    override fun createObserver() {
        viewModel.getBmobUser().observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "SetFragment BmobUser  id  :$it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.settingsDataStore = getSettingsDataStore()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    inner class ProxyClick {
        //选择背景图片
        fun onBackground() {
            viewModel.openFile(SetViewModel.IMAGE_TYPE_BACKGROUND)
        }

        //选择头像
        fun onEditHead() {
            viewModel.openFile(SetViewModel.IMAGE_TYPE_HEAD)
        }

        fun onSaveConfig() {
            if (isInputAllInvalid()) {
                viewModel.saveUserEdit(this@SetFragment) {
                    findNavController().navigateUp()
                }
            }
        }

        fun onEditBirth() {
            viewModel.selectTime(requireContext(), "选择生日", 0, 0, 0) {
                val birthFormat = it.split(" ")[0]
                binding.editBirthEv.text = birthFormat
                Log.v(LOG_TAG, "args.userInfo:${args.userInfo}")
            }
        }
    }

    override fun setEventListener() {
        //选择性别
        val from = LayoutInflater.from(requireContext())
        val popupWindowBinding = SexPopupWindowBinding.inflate(from, null, false)
        val popupWindow = PopupWindow(
            popupWindowBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindowBinding.radioButtonMan.setOnClickListener {
            Log.v(LOG_TAG, "男")
            binding.editGenderEv.text = "男"
            popupWindow.dismiss()
            Log.v(LOG_TAG, "args.userInfo:${args.userInfo}")
        }
        popupWindowBinding.radioButtonWoman.setOnClickListener {
            Log.v(LOG_TAG, "女")
            binding.editGenderEv.text = "女"
            popupWindow.dismiss()
            Log.v(LOG_TAG, "args.userInfo:${args.userInfo}")
        }
        binding.editGenderEv.setOnClickListener {
            popupWindow.showAsDropDown(it, it.width / 8, it.height / 8)
        }
    }

    private fun isInputAllInvalid(): Boolean {
        if (binding.editUsernameEv.text.isEmpty()) {
            showMsgShort("用户名不能为空")
            return false
        }
        if (binding.editSchoolEv.text.isEmpty()) {
            showMsgShort("学校不能为空")
            return false
        }
        if (binding.editCollegeEv.text.isEmpty()) {
            showMsgShort("学院不能为空")
            return false
        }
        if (binding.editDepartmentEv.text.isEmpty()) {
            showMsgShort("系不能为空")
            return false
        }
        if (binding.editPhoneNumberEv.text.isEmpty()) {
            showMsgShort("手机号不能为空")
            return false
        }
        return true
    }
}