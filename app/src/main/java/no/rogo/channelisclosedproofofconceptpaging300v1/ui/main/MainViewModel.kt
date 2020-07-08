package no.rogo.channelisclosedproofofconceptpaging300v1.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val switchEnableGPSUpdateStateMLD:MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>() }

}