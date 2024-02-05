package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Callback.NotificationCallback
import com.example.gethired.Repository.NotificationRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Experience
import com.example.gethired.entities.Notification
import com.example.gethired.entities.NotificationPreference
import com.example.gethired.entities.NotificationRequest

class NotificationViewModel(tokenManager: TokenManager) : ViewModel() {

    private val notificationRepository=NotificationRepository(tokenManager)
    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error


    // LiveData for API responses
//    private val _isNotificationSent = MutableLiveData<Boolean>(null)
//    val isNotificationSent: LiveData<Boolean> = _isNotificationSent

    private val _isAllUnMuted = MutableLiveData<Boolean>(null)
    val isAllUnMuted: LiveData<Boolean> = _isAllUnMuted

    private val _isAllMuted = MutableLiveData<Boolean?>(null)
    val isAllMuted: LiveData<Boolean?> = _isAllMuted

    private val _updatedNotification = MutableLiveData<Notification?>(null)
    val updatedNotification: LiveData<Notification?> = _updatedNotification

    private val _notificationList = MutableLiveData<List<Notification>?>(null)
    val notificationList : LiveData<List<Notification>?> = _notificationList

    private val _notificationPreferencesList = MutableLiveData<List<NotificationPreference>?>(null)
    val notificationPreferencesList : LiveData<List<NotificationPreference>?> = _notificationPreferencesList

    private suspend fun <T> executeApiCall(
        call: suspend () -> ApiResult<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
//        _loading.value = true
//        _error.value = null

        try {
            when (val result = call()) {
                is ApiResult.Success -> onSuccess(result.data!!)
                is ApiResult.Error -> onError(result.exception.localizedMessage ?: "Unknown error")
            }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Unknown error")
        } finally {
//            _loading.value = false
        }
    }
    suspend fun sendNotification(request: NotificationRequest){
        notificationRepository.sendNotification(request)
    }

    suspend fun updateNotification(notificationId:Long){
        executeApiCall(
            call = { notificationRepository.updateNotification(notificationId) },
            onSuccess = { _updatedNotification.value = it },
            onError = { _error.value = it}
        )
    }

    suspend fun deleteNotification(notificationId: Long){
        notificationRepository.deleteNotification(notificationId)
    }

    suspend fun getAllNotification(receiverUsername:String){
        executeApiCall(
            call = { notificationRepository.getAllNotification(receiverUsername) },
            onSuccess = { _notificationList.value = it },
            onError = { _error.value = it}
        )
    }


    suspend fun muteAllNotification(userId: Long){
        executeApiCall(
            call = { notificationRepository.muteAllNotification(userId) },
            onSuccess = { _isAllMuted.value = it },
            onError = { _error.value = it}
        )
    }
    suspend fun unMuteAllNotification(userId: Long){
        executeApiCall(
            call = { notificationRepository.unMuteAllNotification(userId) },
            onSuccess = { _isAllUnMuted.value = it },
            onError = { _error.value = it}
        )
    }

    suspend fun updateNotificationPreference(notificationType:String, userId: Long){
         notificationRepository.updateNotificationPreference(notificationType,userId)
    }

//    suspend fun getNotificationPreference(userId: Long){
//        executeApiCall(
//            call = { notificationRepository.getAllNotificationPreference(userId) },
//            onSuccess = { _notificationPreferencesList.value = it },
//            onError = { _error.value = it }
//        )
//    }

}