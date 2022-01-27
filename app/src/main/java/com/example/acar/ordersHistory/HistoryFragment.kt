package com.example.acar.ordersHistory

import android.location.Geocoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acar.R
import com.example.acar.common.AppModule
import com.example.acar.common.AuthRepository
import com.example.acar.common.DataBaseRepository
import com.example.acar.common.GoogleApiRepository
import com.example.acar.databinding.HistoryFragmentBinding
import com.example.acar.databinding.OrderFragmentBinding
import com.example.acar.login.LoginViewModel
import com.example.acar.order.OrderViewModel
import com.example.acar.order.OrderViewModelFactory
import com.google.android.gms.maps.SupportMapFragment

class HistoryFragment : Fragment() {

    private lateinit var viewModel: OrderViewModel
    private var _binding: HistoryFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var ridesHistoryAdapter: HistoryAdapter
    private  lateinit var rides : ArrayList<RideHistoryItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModelFactory = OrderViewModelFactory(googleApiRepository = GoogleApiRepository(),
            authRepository = AuthRepository(fAuth = AppModule.provideAuthRepo(),
                DataBaseRepository(AppModule.provideFireBaseDBRepo())))
        navController = findNavController()
        val store = navController.getViewModelStoreOwner(R.id.nav_graph_order)
        viewModel = ViewModelProvider(store, viewModelFactory)[OrderViewModel::class.java]
        _binding = HistoryFragmentBinding.inflate(layoutInflater)
        binding.historyToolbar.let {
            it.setNavigationIcon(R.drawable.common_google_signin_btn_icon_dark)
            it.setNavigationOnClickListener { navController.navigate(R.id.action_historyFragment_to_orderFragment) }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeListOfRidesHistory()

        val rvRides = binding.ridesRv
        ridesHistoryAdapter = HistoryAdapter(rides)
        rvRides.adapter = ridesHistoryAdapter
        rvRides.layoutManager = LinearLayoutManager(context)

    }

    private fun observeListOfRidesHistory() {
        rides = arrayListOf()
        viewModel.listOfRidesHistory.observe(viewLifecycleOwner) {
            ridesHistoryAdapter.submitList(it)
        }
    }
}