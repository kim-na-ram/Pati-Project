package com.naram.party_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naram.party_project.chattingModel.ChattingList

class ChattingListViewModel : ViewModel() {
    private val _currentList = MutableLiveData<ArrayList<ChattingList>>()

    val currentList: LiveData<ArrayList<ChattingList>>
        get() = _currentList


    init {
        _currentList.value = arrayListOf()
    }

    fun updateList(list: MutableList<ChattingList>) {
        _currentList.value = list as ArrayList<ChattingList>
    }

//    fun addList(cl: ChattingList) {
//        _currentList.value?.forEach {
//            if (it.chatRoomUID == cl.chatRoomUID
//                && it.lastMessage != cl.lastMessage
//            ) {
//                Log.e("ViewModel", "${cl.lastMessage} and ${cl.timeStamp}")
//                it.lastMessage = cl.lastMessage
//                it.timeStamp = cl.timeStamp
//            }
//        }
//    }
}