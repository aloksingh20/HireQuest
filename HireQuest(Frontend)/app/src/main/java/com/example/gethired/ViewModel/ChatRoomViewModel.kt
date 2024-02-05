package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gethired.Callback.CommonBooleanCallback
import com.example.gethired.Repository.ChatRoomRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Chat
import com.example.gethired.entities.ChatRoom
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class ChatRoomViewModel(tokenManager: TokenManager) : ViewModel() {

    private val chatRoomRepository = ChatRoomRepository(tokenManager)

    private val _chatUserList = MutableLiveData<List<ChatRoom>?>()
    val chatUserList: LiveData<List<ChatRoom>?> = _chatUserList

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _isRequestSent = MutableLiveData(false)
    val isRequestSent: LiveData<Boolean> = _isRequestSent

    private val _isRequestAccepted = MutableLiveData(false)
    val isRequestAccepted: LiveData<Boolean> = _isRequestAccepted

    private val _isRequestDeleted= MutableLiveData(false)
    val isRequestDeleted: LiveData<Boolean> = _isRequestDeleted


    fun getAllChatUser(userId: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                when(val result = chatRoomRepository.getAllChat(userId)){
                    is ApiResult.Success ->{
                        _chatUserList.value = result.data
                    }
                    is ApiResult.Error ->{
                        _error.value=result.exception.message
                    }
                }
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun sendChatRequest(senderId: Long, receiverId: Long, sampleMessage: Chat) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                when(val response = chatRoomRepository.sendChatRequest(senderId, receiverId, sampleMessage)){
                    is ApiResult.Success ->{
                        _isRequestSent.value = response.data
                    }
                    is ApiResult.Error ->{
                        _error.value=response.exception.message
                    }
                }
                // Handle result if needed
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun acceptChatRequest(chatRoomId: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                when(val response = chatRoomRepository.acceptChatRequest(chatRoomId)){
                    is ApiResult.Success ->{
                        _isRequestAccepted.value = response.data
                    }
                    is ApiResult.Error ->{
                        _error.value=response.exception.message
                    }
                }
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteChatRequest(chatRoomId: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                when(val response = chatRoomRepository.deleteChatRequest(chatRoomId)){
                    is ApiResult.Success ->{
                        _isRequestDeleted.value = response.data
                    }
                    is ApiResult.Error ->{
                        _error.value=response.exception.message
                    }
                }
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                _loading.value = false
            }
        }
    }

    private fun handleException(exception: Exception): String {
        return when (exception) {
            is HttpException -> "HTTP error: ${exception.code()}"
            is IOException -> "Network error: ${exception.localizedMessage}"
            else -> "Unexpected error: ${exception.localizedMessage}"
        }
    }
}
