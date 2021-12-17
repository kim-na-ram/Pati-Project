package com.naram.party_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naram.party_project.firebaseModel.ChattingList

class ChattingListViewModel : ViewModel() {
    private val _currentList = MutableLiveData<ArrayList<ChattingList>>()

    val currentList : LiveData<ArrayList<ChattingList>>
        get() = _currentList


    init {
        _currentList.value = arrayListOf()
    }

    fun updateList(list : MutableList<ChattingList>) {
        _currentList.value = list as ArrayList<ChattingList>
    }

}