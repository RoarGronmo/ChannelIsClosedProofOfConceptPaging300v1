package no.rogo.channelisclosedproofofconceptpaging300v1.viewmodels.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import no.rogo.channelisclosedproofofconceptpaging300v1.api.responses.APIGetStationsResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.repository.repositories.CommonDatabaseRepository
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.StationResponse

class MainViewModel() : ViewModel() {

    val switchEnableGPSUpdateStateMLD:MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>() }

    val lastLocationMLD:MutableLiveData<Location?> by lazy { MutableLiveData<Location?>() }

    val getLiveDataPagingDataStationsResponses = CommonDatabaseRepository.getLiveDataPagingDataStationResponse()


}