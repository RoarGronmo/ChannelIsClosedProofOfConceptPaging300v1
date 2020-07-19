package no.rogo.channelisclosedproofofconceptpaging300v1.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import no.rogo.channelisclosedproofofconceptpaging300v1.databinding.MainFragmentBinding
import no.rogo.channelisclosedproofofconceptpaging300v1.databinding.StationListItemBinding
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.StationResponse

/**
 * Created by Roar on 19.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */

class StationAdapter:PagingDataAdapter<StationResponse, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<StationResponse>() {

            val TAG = javaClass.simpleName

            override fun areItemsTheSame(oldItem: StationResponse, newItem: StationResponse): Boolean {

                val isLike =  (
                        (oldItem.killed == newItem.killed)
                                && (oldItem.airDistance == newItem.airDistance)
                                && (oldItem.enterpriseId == newItem.enterpriseId)
                                && (oldItem.airDistance == newItem.airDistance)
                                && (oldItem.enterpriseId == newItem.enterpriseId)
                                && (oldItem.latitude == newItem.latitude)
                                && (oldItem.longitude == newItem.longitude)
                                && (oldItem.stationPrimaryKey == newItem.stationPrimaryKey)
                        )
                Log.i(TAG, "areItemsTheSame: StationAdapter DiffUtil contents: $oldItem == $newItem: $isLike")

                return isLike
            }

            override fun areContentsTheSame(oldItem: StationResponse, newItem: StationResponse): Boolean {
                Log.i(TAG, "areContentsTheSame: StationAdapter DiffUtil: $newItem == $oldItem : ${oldItem == newItem}")
                return oldItem == newItem
            }

        }
){
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //TODO("Not yet implemented")

        val viewHolder = ViewHolder(
                StationListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false)
        )

        return viewHolder
    }

    class ViewHolder(
            private val stationListItemBinding: StationListItemBinding
    ):RecyclerView.ViewHolder(stationListItemBinding.root)
    {
        fun binding(

        )
        {

        }

        fun clear()
        {

        }
    }

}