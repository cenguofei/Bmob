package com.example.bmob.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.bmob.data.entity.USER_HAS_NOT_IDENTIFICATION

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


/**
 * 使用DataStore存储用户配置信息
 *  例如，记住密码选项，保存用户账号密码等
 *  学习文档：
 *  https://developer.android.google.cn/codelabs/basic-android-kotlin-training-preferences-datastore?hl=zh_cn#0
 */
class SettingsDataStore private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: SettingsDataStore? = null
        @Volatile
        private var CONTEXT: Context? = null

        /**
         * 单例模式，获取实例
         * 必须要设置单例，因为一个FragmentA进入另一个FragmentB再返回FragmentA时，A就会实例化两个DataStore对象，
         * 而这种情况是不允许的
         */
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            if (INSTANCE == null) {
                CONTEXT = context
                INSTANCE = SettingsDataStore()
            }
            INSTANCE!!
        }
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = SETTINGS_PREFERENCES_NAME
    )

    //调用 edit() 函数时不要忘记将函数设为 suspend
    suspend fun saveConfigToPreferencesStore(
        isRememberPassword: Boolean,
        userName: String,
        pwd: String,
        context: Context
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_REMEMBER_PASSWORD] = isRememberPassword
            preferences[USERNAME] = userName
            preferences[PASSWORD] = pwd
        }
    }

    suspend fun saveIdentificationToPreferencesStore(identification: Int, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[USER_IDENTIFICATION] = identification
        }
    }

    suspend fun saveUsernameToPreferencesStore(username: String, context: Context) {
        context.dataStore.edit { preferenceFlow ->
            preferenceFlow[USERNAME] = username
        }
    }

    val preferenceFlow: Flow<UserConfig> = CONTEXT!!.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val isRememberPassword = preferences[IS_REMEMBER_PASSWORD] ?: false
            val username = preferences[USERNAME] ?: ""
            val password = preferences[PASSWORD] ?: ""
            return@map UserConfig(isRememberPassword, username, password)
        }

    val userIdentification: Flow<Int> = CONTEXT!!.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            it[USER_IDENTIFICATION] ?: USER_HAS_NOT_IDENTIFICATION
        }
}

data class UserConfig(
    val isRememberPassword: Boolean, //是否记住密码
    val username: String,
    val password: String
)

private const val SETTINGS_PREFERENCES_NAME = "settings_preferences"

//保存用户是否需要记住密码
private val IS_REMEMBER_PASSWORD = booleanPreferencesKey("is_remember_password")

//保存用户名
private val USERNAME = stringPreferencesKey("username")

//保存用户密码
private val PASSWORD = stringPreferencesKey("password")

//保存用户身份
private val USER_IDENTIFICATION = intPreferencesKey("user_identification")