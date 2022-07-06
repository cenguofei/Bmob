package com.example.bmob.fragments.mine

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentMineBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SetViewModel
import kotlinx.parcelize.Parcelize
import okhttp3.internal.notify
import okhttp3.internal.notifyAll


class MineFragment : Fragment() ,FragmentEventListener{
    private lateinit var binding:FragmentMineBinding
    private val setViewModel:SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater,container,false)

        setViewModel.getBmobUser().observe(viewLifecycleOwner){
            Log.v(LOG_TAG,"MineFragment BmobUser  id  :$it")
            binding.bmobUser = it
        }
        setViewModel.getUserByQuery().observe(viewLifecycleOwner){
            Log.v(LOG_TAG,"MineFragment User  id  :$it")
            binding.user = it
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    //设置点击事件
    override fun setEventListener(){
        binding.editUserInfoIv.setOnClickListener {
            findNavController().navigate(R.id.action_mineFragment_to_setFragment)
        }
        binding.reLogin.setOnClickListener {
            BmobUser.logOut()
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }
        binding.exchangeAccount.setOnClickListener {
            val actionMineFragmentToLoginFragment =
                MineFragmentDirections.actionMineFragmentToLoginFragment(true)
            findNavController().navigate(actionMineFragmentToLoginFragment)
        }
        binding.editUserInfoIv.setOnClickListener {
            val userInfo = SetUserInfo()
            with(setViewModel.getUserByQuery()){
                value?.backgroundUrl.let { userInfo.backgroundUrl = it }
                value?.avatarUrl.let { userInfo.avatarUrl = it }
                value?.name.let { userInfo.name = it }
                value?.signature.let { userInfo.signature = it }
                value?.age.let { userInfo.age = it }
                value?.gender.let { userInfo.gender = it }
                value?.username.let { userInfo.username = it }
                value?.school.let { userInfo.school = it }
                value?.college.let { userInfo.college = it }
                value?.department.let { userInfo.department = it }
                value?.birth.let { userInfo.birth = it }
                value?.address.let { userInfo.address = it }
            }
            with(setViewModel.getBmobUser()){
                value?.mobilePhoneNumber.let { userInfo.phoneNumber = it }
                value?.email.let { userInfo.email = it }
            }
            val actionMineFragmentToSetFragment =
                MineFragmentDirections.actionMineFragmentToSetFragment(userInfo)
            findNavController().navigate(actionMineFragmentToSetFragment)
        }
    }
    companion object{
        const val QUERY_USER_KEY = "_query"
        const val BMOB_USER_KEY = "_bmob"
    }
}

@Parcelize
data class SetUserInfo(
    var backgroundUrl:String? = null,
    var avatarUrl:String? = null,
    var name:String? = null,
    var signature:String? = null,
    var age:Int? = null,
    var gender:String? = null,
    var username:String? = null,  //即工号
    var school:String? = null,
    var college:String? = null,
    var department:String? = null,
    var birth:String? = null,
    var phoneNumber:String? = null,
    var email:String? = null,
    var address:String? = null
):Parcelable