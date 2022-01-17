package com.example.acar.ordersHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.acar.R
import com.example.acar.databinding.ItemHistoryOrderBinding
import com.example.acar.order.OrderViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class HistoryAdapter(private val historyOfRides: ArrayList<RideHistoryItem>, private val viewModel: OrderViewModel) :
    ListAdapter<RideHistoryItem, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    class HistoryDiffCallback : DiffUtil.ItemCallback<RideHistoryItem>() {
        override fun areItemsTheSame(oldItem: RideHistoryItem, newItem: RideHistoryItem): Boolean {
            return oldItem.polyLineOverview == newItem.polyLineOverview
        }

        override fun areContentsTheSame(oldItem: RideHistoryItem, newItem: RideHistoryItem): Boolean {
            return oldItem.polyLineOverview == newItem.polyLineOverview
        }
    }

    inner class ViewHolder(private val itemBinding: ItemHistoryOrderBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(ride: RideHistoryItem) {
            itemBinding.dateTv.text = ride.date
            itemBinding.pickupTv.text = ride.pickup
            itemBinding.destinationTv.text = ride.destination
            Glide
                    .with(itemBinding.root)
                    .load(
                        "https://maps.googleapis.com/maps/api/staticmap?size=400x400&center=59.900503,-135.478011&zoom=4&path=weight:3%7Ccolor:orange%7Cenc:_fisIp~u%7CU}%7Ca@pytA_~b@hhCyhS~hResU%7C%7Cx@oig@rwg@amUfbjA}f[roaAynd@%7CvXxiAt{ZwdUfbjAewYrqGchH~vXkqnAria@c_o@inc@k{g@i`]o%7CF}vXaj\\h`]ovs@?yi_@rcAgtO%7Cj_AyaJren@nzQrst@zuYh`]v%7CGbldEuzd@%7C%7Cx@spD%7CtrAzwP%7Cd_@yiB~vXmlWhdPez\\_{Km_`@~re@ew^rcAeu_@zhyByjPrst@ttGren@aeNhoFemKrvdAuvVidPwbVr~j@or@f_z@ftHr{ZlwBrvdAmtHrmT{rOt{Zz}E%7Cc%7C@o%7CLpn~AgfRpxqBfoVz_iAocAhrVjr@rh~@jzKhjp@``NrfQpcHrb^k%7CDh_z@nwB%7Ckb@a{R%7Cyh@uyZ%7CllByuZpzw@wbd@rh~@%7C%7CFhqs@teTztrAupHhyY}t]huf@e%7CFria@o}GfezAkdW%7C}[ocMt_Neq@ren@e~Ika@pgE%7Ci%7CAfiQ%7C`l@uoJrvdAgq@fppAsjGhg`@%7ChQpg{Ai_V%7C%7Cx@mkHhyYsdP%7CxeA~gF%7C}[mv`@t_NitSfjp@c}Mhg`@sbChyYq}e@rwg@atFff}@ghN~zKybk@fl}A}cPftcAite@tmT__Lha@u~DrfQi}MhkSqyWivIumCria@ciO_tHifm@fl}A{rc@fbjAqvg@rrqAcjCf%7Ci@mqJtb^s%7C@fbjA{wDfs`BmvEfqs@umWt_Nwn^pen@qiBr`xAcvMr{Zidg@dtjDkbM%7Cd_@&key=AIzaSyCFFrK_PjTsfjaPx3LW12JlPpB1rc-JIt0")
                    .into(itemBinding.mapImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // TODO: 17.01.2022  history of rides in viewmodel as alist   historyOfRides = viewmodle.list. so its initialised early
        submitList(historyOfRides)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rideItem = getItem(position)
        holder.bind(rideItem)
    }
}