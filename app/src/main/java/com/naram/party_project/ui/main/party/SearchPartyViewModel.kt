package com.naram.party_project.ui.main.party

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naram.party_project.data.remote.model.PartyFirebase

class SearchPartyViewModel : ViewModel() {

    private val _currentList = MutableLiveData<ArrayList<PartyFirebase>>()

    val currentList : LiveData<ArrayList<PartyFirebase>>
        get() = _currentList


    init {
        _currentList.value = arrayListOf()
    }

    fun updateList(list : MutableList<PartyFirebase>) {
        _currentList.value = list as ArrayList<PartyFirebase>
    }

}