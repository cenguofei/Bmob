package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.ApproveThesisItemBinding
import com.example.bmob.databinding.FragmentApprovedNotApprovedBinding
import com.example.bmob.databinding.ItemApprovedNotApprovedThesisBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.ApprovedNotApprovedViewModel
import com.example.bmob.viewmodels.SetViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * 系主任首页点击 审批课题
 */
class ApprovedNotApprovedFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentApprovedNotApprovedBinding
    private val viewModel: ApprovedNotApprovedViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()
    private var currentPagePos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApprovedNotApprovedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dean = setViewModel.getUserByQuery().value
        setEventListener()
        initViewPager()
        viewModel.getApprovedThesisList(setViewModel.getUserByQuery().value!!) {
            showMsg(requireContext(), it)
        }.observe(viewLifecycleOwner) {
            if (currentPagePos == HAS_APPROVED_PAGE) {
                if (it != null && it.isNotEmpty()) {
                    fragments[currentPagePos].binding.recyclerView.run {
                        adapter = ApproveThesisAdapter(
                            it
                        ) { thesis ->
                            Log.v(LOG_TAG, "教师的${thesis.title}被点击了:$thesis")
                        }
                        layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    }
                }
            }
        }
    }

    override fun setEventListener() {

    }

    private fun initViewPager() {
        val tabs = listOf(
            "已审批课题", "待审批课题"
        )
        val pagerAdapter =
            ViewPagerAdapter(requireContext(), fragments!!, parentFragmentManager, lifecycle)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(
            binding.resultsTabLayout, binding.viewPager
        ) { tab, position -> tab.text = tabs[position] }.attach()

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    //筛选对应页面的内容
                    if (position == HAS_APPROVED_PAGE) {
                        //已审批课题
                        currentPagePos = HAS_APPROVED_PAGE
                        viewModel.getApprovedThesisList(setViewModel.getUserByQuery().value!!) {
                            showMsg(requireContext(), it)
                        }
                    } else {
                        currentPagePos = NO_APPROVED_PAGE
                    }
                }
            }
        )
    }

    companion object {
        const val HAS_APPROVED_PAGE = 0
        const val NO_APPROVED_PAGE = 1
        private val fragments = listOf(
            ViewPagerFragment(), ViewPagerFragment()
        )
    }
}

class ApproveThesisAdapter(
    private val data: MutableList<MutableList<Thesis>>,
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
            thesisList.forEach { thesis ->
                val from = LayoutInflater.from(parent.context)
                val itemBinding =
                    ItemApprovedNotApprovedThesisBinding.inflate(from, parent, false)
                itemBinding.tile = thesis.title
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
}
