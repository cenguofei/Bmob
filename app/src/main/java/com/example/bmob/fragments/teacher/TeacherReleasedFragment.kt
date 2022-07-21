package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentTeacherReleasedBinding
import com.example.bmob.databinding.ItemTeacherReleaseBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.TeacherThesisViewModel


class TeacherReleasedFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentTeacherReleasedBinding
    private val thesisViewModel: TeacherThesisViewModel by activityViewModels()
    private val setViewModel: SetViewModel by activityViewModels()


    private var adapter:RecyclerViewAdapter<Thesis>? = null

    private val itemTouchHelper = ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.LEFT,
        ItemTouchHelper.LEFT
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            return false
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

        }

        /**
         * 触发OnSwiped的滑动阈值
         */
        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return Float.MAX_VALUE
        }
        /**
         * 触发OnSwiped的滑动速度
         */
        override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return Float.MAX_VALUE
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.LEFT){
                val pos = viewHolder.adapterPosition
                thesisViewModel.getThesisList(null).value?.let {
                    it.removeAt(pos)
                    val newThesisList = it.toMutableList()
                    thesisViewModel.setThesisList(newThesisList)
                    adapter?.setData(newThesisList)
                }
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherReleasedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()

        thesisViewModel.getThesisList(setViewModel.getUserByQuery().value!!)
            .observe(viewLifecycleOwner) {
                Log.v(LOG_TAG,"getThesisList")
                RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
                    val itemInflater = LayoutInflater.from(parent.context)
                    RecyclerViewAdapter.ResultViewHolder(
                        ItemTeacherReleaseBinding.inflate(
                            itemInflater,
                            parent,
                            false
                        )
                    )
                }
                adapter = RecyclerViewAdapter(it) { binding, result ->
                    (binding as ItemTeacherReleaseBinding).run {
                        thesis = result
                        val actionTeacherReleasedFragmentToTeacherNewThesisFragment =
                            TeacherReleasedFragmentDirections
                                .actionTeacherReleasedFragmentToTeacherNewThesisFragment(true)
                        root.setOnClickListener {
                            thesisViewModel.setThesis(result)
                            findNavController().navigate(actionTeacherReleasedFragmentToTeacherNewThesisFragment)
                        }
                    }
                }
                binding.recyclerView2.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                binding.recyclerView2.adapter = adapter
                itemTouchHelper.attachToRecyclerView(binding.recyclerView2)
            }
    }

    override fun setEventListener() {
        binding.addNewThesis.setOnClickListener {
            findNavController().navigate(R.id.action_teacherReleasedFragment_to_teacherNewThesisFragment)
        }
        binding.imageView12.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}