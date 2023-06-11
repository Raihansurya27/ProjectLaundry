package com.raihan.projectlaundry.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.raihan.projectlaundry.activitity.CalculationActivity
import com.raihan.projectlaundry.adapter.GridMainAdapter
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.databinding.FragmentHomeBinding
import com.raihan.projectlaundry.model.ServiceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var gridList: List<ServiceModel>
    private lateinit var gridAdapter: GridMainAdapter
    val apiService = ApiClient.apiService
    private lateinit var grid: GridView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        grid = binding.gridHome

        gridList = ArrayList<ServiceModel>()
        getData()
        val result = arguments
//        if (result != null) {
//            val navController = findNavController()
//            navController.navigate(R.id.action_nav_home_to_nav_gallery, result)
//        }
        grid.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val bundle = Bundle()
            val intent = Intent(requireContext(), CalculationActivity::class.java)
            bundle.putString("service", gridList[i].name)
            bundle.putString("service_id", gridList[i].service_id)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    fun getData() {
        apiService.getAllService().enqueue(object : Callback<List<ServiceModel>> {
            override fun onResponse(
                call: Call<List<ServiceModel>>,
                response: Response<List<ServiceModel>>
            ) {
                if (response.isSuccessful) {
                    val services = response.body()!!

                    services!!.let {
                        gridList = it

                        gridAdapter = GridMainAdapter(gridList, requireContext())
                        grid.adapter = gridAdapter
                    }

                } else {

                }
            }

            override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                //
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(bundle: Bundle): HomeFragment {
            val fragment = HomeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}