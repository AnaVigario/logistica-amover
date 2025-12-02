package com.example.myamover.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.dao.UserDao
import kotlinx.coroutines.launch

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,

) {
}

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _name = mutableStateOf<String?>(null)
    val name: String? = _name.value

    fun loadUser(email: String) {
        viewModelScope.launch {
            val user = userDao.getByemail(email)
            _name.value = user?.name
        }
    }
}