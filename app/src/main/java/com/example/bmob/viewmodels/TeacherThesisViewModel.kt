package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.data.entity.SELECT_STATE_UNSELECTED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentTeacherNewThesisBinding
import com.example.bmob.utils.*

class TeacherThesisViewModel(private val handler: SavedStateHandle) : ViewModel() {

    //选中的Thesis
    private var _selectedThesis = MutableLiveData<Thesis>()

    fun setThesis(thesis: Thesis) {
        _selectedThesis.value = thesis
    }

    var selectedThesis = _selectedThesis

    //教师上传一个课题后就显示到 我的课题 界面
    fun addThesis(user: User, thesis: Thesis) {
        val thesisList = getThesisList(user).value
        thesisList?.add(thesis)
        handler.set(CURRENT_TEACHER_THESIS, thesisList)
    }

    fun setThesisList(listThesis: MutableList<Thesis>) {
        handler.set(CURRENT_TEACHER_THESIS, listThesis)
    }

    fun getThesisList(user: User?): MutableLiveData<MutableList<Thesis>> {
        if (!handler.contains(CURRENT_TEACHER_THESIS)) {
            queryTeacherAllThesis(user!!) { isSuccess, thesisList, msg ->
                if (isSuccess) {
                    handler.set(CURRENT_TEACHER_THESIS, thesisList)
                } else {
                    Log.v(LOG_TAG, "教师课题查询失败：$msg")
                }
            }
        }
        return handler.getLiveData(CURRENT_TEACHER_THESIS)
    }

    /**
     * 查询该教师的所有课题
     */
    private fun queryTeacherAllThesis(
        user: User,
        callback: (isSuccess: Boolean, thesisList: MutableList<Thesis>?, msg: String) -> Unit
    ) {
        BmobQuery<Thesis>().run {
            and(ArrayList<BmobQuery<Thesis>>().apply {
                add(BmobQuery<Thesis>().addWhereEqualTo(School, user.school))
                add(BmobQuery<Thesis>().addWhereEqualTo(Department, user.department))
                add(BmobQuery<Thesis>().addWhereEqualTo(College, user.college))
                add(BmobQuery<Thesis>().addWhereEqualTo(TeacherId, user.objectId))
            })
        }.findObjects(object : FindListener<Thesis>() {
            override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                if (p1 == null) {
                    if (p0 == null) {
                        callback.invoke(false, null, "没有搜索到教师课题")
                    } else {
                        callback.invoke(true, p0, EMPTY_TEXT)
                    }
                } else {
                    callback.invoke(false, null, p1.message.toString())
                }
            }
        })
    }

    /**
     * 保存课题到数据库
     */
    fun saveThesis(
        user: User,
        thesis: Thesis,
        callback: (isSuccess: Boolean, msg: String) -> Unit
    ) {
        thesis.run {
            teacherId = user.objectId
            teacherAvatarUrl = user.avatarUrl
            teacherName = user.name
            selectState = SELECT_STATE_UNSELECTED
            school = user.school
            college = user.college
            department = user.department
            enabledToStudent = false
            thesisState = NOT_APPROVED  //没有审批
        }
        thesis.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 == null) {
                    addThesis(user, thesis)
                    callback.invoke(true, EMPTY_TEXT)
                } else {
                    callback.invoke(false, p1.message.toString())
                }
            }
        })
    }

    /**
     * 输入合法才保存
     */
    fun isInputValid(binding: FragmentTeacherNewThesisBinding): Boolean {
        return binding.thesisTitle.text.isNotEmpty()
                && binding.thesisField.text.isNotEmpty()
                && binding.thesisBrief.text.isNotEmpty()
                && binding.thesisRequire.text.isNotEmpty()
    }

    fun updateThesis(
        user: User,
        thesis: Thesis,
        callback: (isSuccess: Boolean, msg: String) -> Unit
    ) {
        thesis.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    val mutableList = getThesisList(user).value
                    mutableList?.forEach {
                        if (it.objectId == thesis.objectId) {
                            mutableList.remove(it)
                        }
                    }
                    mutableList?.add(0, thesis)
                    handler.set(CURRENT_TEACHER_THESIS, mutableList)
                    callback.invoke(true, EMPTY_TEXT)
                } else {
                    callback.invoke(false, p0.message.toString())
                }
            }
        })
    }

    companion object {
        private const val CURRENT_TEACHER_THESIS = "_teacher_thesis"
    }
}