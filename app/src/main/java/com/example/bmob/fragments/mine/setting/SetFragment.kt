package com.example.bmob.fragments.mine.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentSetBinding
import com.example.bmob.databinding.SexPopupWindowBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.IMAGE_TYPE_BACKGROUND
import com.example.bmob.viewmodels.IMAGE_TYPE_HEAD
import com.example.bmob.viewmodels.SetViewModel

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
class SetFragment : Fragment() ,FragmentEventListener{
    lateinit var binding:FragmentSetBinding
    private val viewModel:SetViewModel by activityViewModels()
    private val args:SetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setRegister(this)
        Log.v(LOG_TAG,"SetFragment  viewModel $viewModel")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetBinding.inflate(inflater,container,false)
        binding.user = args.userInfo
        viewModel.setSettingsDataStore(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()

        viewModel.getBmobUser().observe(viewLifecycleOwner){
            Log.v(LOG_TAG,"SetFragment BmobUser  id  :$it")
        }
        viewModel.getUserByQuery().observe(viewLifecycleOwner){
            Log.v(LOG_TAG,"SetFragment User  id  :$it")
        }
    }

    override fun setEventListener() {
        //选择背景图片
        binding.backgroundIv.setOnClickListener {
            viewModel.openFile(IMAGE_TYPE_BACKGROUND)
        }
        //选择头像
        binding.editHeadIv.setOnClickListener {
            viewModel.openFile(IMAGE_TYPE_HEAD)
        }
        binding.saveConfigBtn.setOnClickListener {
            if (isInputAllInvalid()){
                viewModel.saveUserEdit(this)
            }
        }
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
            Log.v(LOG_TAG,"男")
            binding.editGenderEv.text = "男"
            popupWindow.dismiss()
        }
        popupWindowBinding.radioButtonWoman.setOnClickListener {
            Log.v(LOG_TAG,"女")
            binding.editGenderEv.text = "女"
            popupWindow.dismiss()
        }
        binding.editGenderEv.setOnClickListener {
            popupWindow.showAsDropDown(it,it.width/8,it.height/8)
        }
        binding.editBirthEv.setOnClickListener {
            viewModel.selectTime(requireContext(),"选择生日",0,0,0){
                binding.editBirthEv.text = it
            }
        }
    }
    private fun isInputAllInvalid():Boolean{
        if (binding.editUsernameEv.text.isEmpty()){
            showMsg(requireContext(),"用户名不能为空")
            return false
        }
        if (binding.editSchoolEv.text.isEmpty()){
            showMsg(requireContext(),"学校不能为空")
            return false
        }
        if (binding.editCollegeEv.text.isEmpty()){
            showMsg(requireContext(),"学院不能为空")
            return false
        }
        if (binding.editDepartmentEv.text.isEmpty()){
            showMsg(requireContext(),"系不能为空")
            return false
        }
        if (binding.editPhoneNumberEv.text.isEmpty()){
            showMsg(requireContext(),"手机号不能为空")
            return false
        }
        return true
    }
}