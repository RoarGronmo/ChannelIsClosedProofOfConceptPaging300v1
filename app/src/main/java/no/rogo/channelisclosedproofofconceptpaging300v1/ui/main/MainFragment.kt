package no.rogo.channelisclosedproofofconceptpaging300v1.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import kotlinx.android.synthetic.main.main_fragment.*
import no.rogo.channelisclosedproofofconceptpaging300v1.R
import no.rogo.channelisclosedproofofconceptpaging300v1.viewmodels.main.MainViewModel

class MainFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: (view = $view, savedInstanceState = $savedInstanceState)")
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        mainViewModel.lastLocationMLD.observe(viewLifecycleOwner){location ->
            location?.let { safeLocation ->
                textViewLocation.text=getString(R.string.locationText,safeLocation.latitude,safeLocation.longitude)
            }
        }

        switch_enable_gps_update.setOnClickListener {

            it as Switch

            mainViewModel.switchEnableGPSUpdateStateMLD.postValue(it.isChecked)

            Log.d(TAG, "onActivityCreated: it.isChecked = ${it.isChecked}")
        }
    }

}