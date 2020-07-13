package no.rogo.channelisclosedproofofconceptpaging300v1.viewmodels.main

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val switchEnableGPSUpdateStateMLD:MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>() }

    val lastLocationMLD:MutableLiveData<Location?> by lazy { MutableLiveData<Location?>() }

}