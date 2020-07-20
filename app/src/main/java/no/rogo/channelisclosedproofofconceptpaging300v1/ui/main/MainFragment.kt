package no.rogo.channelisclosedproofofconceptpaging300v1.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.rogo.channelisclosedproofofconceptpaging300v1.R
import no.rogo.channelisclosedproofofconceptpaging300v1.databinding.MainFragmentBinding
import no.rogo.channelisclosedproofofconceptpaging300v1.repository.repositories.CommonDatabaseRepository
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.adapters.StationAdapter
import no.rogo.channelisclosedproofofconceptpaging300v1.viewmodels.main.MainViewModel
import javax.security.auth.login.LoginException

class MainFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    private var viewModelJob: Job? =null
    lateinit var adapter:StationAdapter

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        adapter = StationAdapter()

        val binding = MainFragmentBinding.inflate(inflater,container, false)

        //return inflater.inflate(R.layout.main_fragment, container, false)

        binding.stationRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: (view = $view, savedInstanceState = $savedInstanceState)")
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        mainViewModel.lastLocationMLD.observe(viewLifecycleOwner){location ->
            location?.let { safeLocation ->
                textViewLocation.text=getString(
                        R.string.locationText,
                        safeLocation.latitude,
                        safeLocation.longitude)
                CommonDatabaseRepository.insertLocation(safeLocation)
            }
        }

        switch_enable_gps_update.setOnClickListener {

            it as Switch

            mainViewModel.switchEnableGPSUpdateStateMLD.postValue(it.isChecked)

            Log.d(TAG, "onActivityCreated: it.isChecked = ${it.isChecked}")
        }

        mainViewModel.getLiveDataPagingDataStationsResponses?.observe(viewLifecycleOwner){
            pagingDataStationResponse ->
            Log.i(TAG, "onViewCreated: mainViewModel.getLiveDataPagingDataStationsResponses observing pagingDataResponse.size = $pagingDataStationResponse")
            viewModelJob?.cancel()
            viewModelJob = lifecycleScope.launch {
                Log.i(TAG, "onViewCreated: viewModelJob submitting data to adapter")
                adapter.submitData(pagingDataStationResponse)
            }
        }


    }

}