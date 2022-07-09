package com.example.bmob.fragments.dean.approve

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.ApproveThesisItemBinding
import com.example.bmob.databinding.ItemApprovedNotApprovedThesisBinding

//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import androidx.viewpager2.widget.ViewPager2
//import com.example.bmob.R
//import com.example.bmob.common.FragmentEventListener
//import com.example.bmob.data.entity.NOT_APPROVED
//import com.example.bmob.data.entity.Thesis
//import com.example.bmob.databinding.ApproveThesisItemBinding
//import com.example.bmob.databinding.FragmentApprovedNotApprovedBinding
//import com.example.bmob.databinding.ItemApprovedNotApprovedThesisBinding
//import com.example.bmob.utils.LOG_TAG
//import com.example.bmob.utils.showMsg
//import com.example.bmob.viewmodels.ApprovedNotApprovedViewModel
//import com.example.bmob.viewmodels.SetViewModel
//import com.google.android.material.tabs.TabLayoutMediator
//
///**
// * 系主任首页点击 审批课题
// */
//class ApprovedNotApprovedFragment : Fragment(), FragmentEventListener {
//    private lateinit var binding: FragmentApprovedNotApprovedBinding
//    private val viewModel: ApprovedNotApprovedViewModel by viewModels()
//    private val setViewModel: SetViewModel by activityViewModels()
//    private var approveThesisAdapter: ApproveThesisAdapter? = null
//    private var notApproveThesisAdapter: ApproveThesisAdapter? = null
//
//
//    private var approvedData:MutableList<MutableList<Thesis>>? = null
//    private var notApprovedData:MutableList<MutableList<Thesis>>? = null
//
//    /**
//     * 不能协程静态变量
//     * 不然会报：
//     *    java.lang.IllegalStateException: Fragment already added
//     */
//    private val fragments = listOf(ViewPagerFragment(), ViewPagerFragment())
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentApprovedNotApprovedBinding.inflate(inflater, container, false)
////        viewModel.currentLiveData.observe(viewLifecycleOwner){
////            when(viewModel.getCurrentPos()){
////                0 -> {
////                    if (approveThesisAdapter == null){
////                        approveThesisAdapter = ApproveThesisAdapter(it){thesis->
////                            Log.v(LOG_TAG,"approveThesisAdapter 被点击了：$thesis")
////                        }
////                        fragments[0].binding.recyclerView.run {
////                            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
////                            adapter = approveThesisAdapter
////                        }
////                    }else{
////                        approveThesisAdapter!!.setData(it)
////                    }
////                }
////                1 -> {
////                    if (notApproveThesisAdapter == null){
////                        notApproveThesisAdapter = ApproveThesisAdapter(it){thesis->
////                            Log.v(LOG_TAG,"approveThesisAdapter 被点击了：$thesis")
////                        }
////                        fragments[0].binding.recyclerView.run {
////                            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
////                            adapter = notApproveThesisAdapter
////                        }
////                    }else{
////                        notApproveThesisAdapter!!.setData(it)
////                    }
////                }
////            }
////        }
//        viewModel.getApprovedThesisList(setViewModel.getUserByQuery().value!!){
//            showMsg(requireContext(),it)
//        }.observe(viewLifecycleOwner){
//            approvedData = it
//        }
//        viewModel.getNotApprovedThesisList(setViewModel.getUserByQuery().value!!){
//            showMsg(requireContext(),it)
//        }.observe(viewLifecycleOwner){
//            notApprovedData = it
//        }
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.dean = setViewModel.getUserByQuery().value
//        setEventListener()
//        initViewPager()
//    }
//
//    override fun setEventListener() {
//        binding.backImg.setOnClickListener {
//            findNavController().navigateUp()
//        }
//    }
//
//    private fun initViewPager() {
//        val tabs = listOf(
//            "已审批课题", "待审批课题"
//        )
//
//        /**
//         * parentFragmentManager，这里由于Fragment里面嵌套ViewPager2，ViewPager2里面又有Fragment
//         * 所以要用childFragmentManager，不能用parentFragmentManager，
//         * 否则会报下面的错误：
//         *        java.lang.IllegalStateException: Fragment already added
//         */
//        val pagerAdapter =
//            ViewPagerAdapter(requireContext(), fragments, childFragmentManager, lifecycle)
//        binding.viewPager.adapter = pagerAdapter
//        TabLayoutMediator(
//            binding.resultsTabLayout, binding.viewPager
//        ) { tab, position -> tab.text = tabs[position] }.attach()
//
//        binding.viewPager.registerOnPageChangeCallback(
//            object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    //筛选对应页面的内容
//                    //已审批课题
////                    viewModel.setCurrentPos(position)
//                    Log.v(LOG_TAG,"position=$position")
//                    if (position == 0){
//                        if (approvedData != null){
//                            if (approveThesisAdapter == null){
//                                approveThesisAdapter = ApproveThesisAdapter(approvedData!!){thesis->
//                                    Log.v(LOG_TAG,"approveThesisAdapter 被点击了：$thesis")
//                                }
//                                fragments[0].binding.recyclerView.run {
//                                    layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
//                                    adapter = approveThesisAdapter
//                                }
//                            }else{
//                                Log.v(LOG_TAG,"approveThesisAdapter!!.setData(approvedData!!)")
//                                approveThesisAdapter!!.setData(approvedData!!)
//                            }
//                        }
//                    }else{
//                        if (notApprovedData != null){
//                            if (notApproveThesisAdapter == null){
//                                notApproveThesisAdapter = ApproveThesisAdapter(notApprovedData!!){thesis->
//                                    Log.v(LOG_TAG,"approveThesisAdapter 被点击了：$thesis")
//                                }
//                                fragments[0].binding.recyclerView.run {
//                                    layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
//                                    adapter = notApproveThesisAdapter
//                                }
//                            }else{
//                                Log.v(LOG_TAG,"notApproveThesisAdapter!!.setData(notApprovedData!!)")
//                                notApproveThesisAdapter!!.setData(notApprovedData!!)
//                            }
//                        }
//                    }
//                }
//            }
//        )
//    }
//}

class ApproveThesisAdapter(
    private var data: MutableList<MutableList<Thesis>>,
    private val teacherThesisOnClickCallback: (teacherThesis: Thesis) -> Unit
) : RecyclerView.Adapter<ApproveThesisAdapter.ApproveThesisAdapterViewHolder>() {

    class ApproveThesisAdapterViewHolder(val binding: ApproveThesisItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun createViewHolder(parent: ViewGroup): ApproveThesisAdapterViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = ApproveThesisItemBinding.inflate(from, parent, false)
                return ApproveThesisAdapterViewHolder(itemBinding)
            }
        }

        fun bind(
            holder: ApproveThesisAdapterViewHolder,
            thesisList: MutableList<Thesis>,
            teacherThesisOnClickCallback: (teacherThesis: Thesis) -> Unit,
            parent: ViewGroup
        ) {
            holder.binding.thesisTeacherName = thesisList[0].teacherName
            thesisList.forEach { thesis ->
                val from = LayoutInflater.from(parent.context)
                val itemBinding =
                    ItemApprovedNotApprovedThesisBinding.inflate(from, parent, false)
                itemBinding.tile = thesis.title
                itemBinding.desc = thesis.description
                itemBinding.root.setOnClickListener {
                    teacherThesisOnClickCallback.invoke(thesis)
                }
                holder.binding.itemContainer.addView(itemBinding.root)
            }
            holder.binding.itemContainer
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ApproveThesisAdapterViewHolder {
        return ApproveThesisAdapterViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ApproveThesisAdapterViewHolder, position: Int) {
        holder.bind(
            holder,
            data[position],
            teacherThesisOnClickCallback,
            holder.binding.rootLinearLayout
        )

    }

    override fun getItemCount(): Int = data.size

    fun setData(newData: MutableList<MutableList<Thesis>>) {
        this.data = newData
        this.notifyDataSetChanged()
    }
}
