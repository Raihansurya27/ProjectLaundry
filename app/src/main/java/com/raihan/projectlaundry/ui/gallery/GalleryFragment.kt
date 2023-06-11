package com.raihan.projectlaundry.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.raihan.projectlaundry.adapter.GridHistoryAdapter
import com.raihan.projectlaundry.databinding.FragmentGalleryBinding
import com.raihan.projectlaundry.model.GridHistoryModel

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var gridList1: List<GridHistoryModel>
    private lateinit var gridList2: List<GridHistoryModel>
    private lateinit var gridAdapter: GridHistoryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gridDone: GridView = binding.gridDone
        val gridDoing: GridView = binding.gridDoing
        val textTitle:TextView = binding.txtTitle

//        val bundle :Bundle? = arguments
//        if (bundle != null){
//            textTitle.setText(bundle.getString("username"))
//        }

        val bundle = arguments
        if (bundle != null) {
            val value = bundle.getString("username")
            textTitle.setText(value)
            // Lakukan sesuatu dengan nilai yang diterima
        }

        gridList1 = ArrayList<GridHistoryModel>()
        gridList1 = gridList1 + GridHistoryModel("#Cuci1","12/05/2023","Setrika")
        gridList1 = gridList1 + GridHistoryModel("#Cuci3","12/05/2023","Setrika")
        gridAdapter = GridHistoryAdapter(gridList1,requireContext())
        gridDoing.adapter = gridAdapter
        gridDoing.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i, l ->
            Toast.makeText(requireContext(),gridList1[i].entryCode,Toast.LENGTH_SHORT).show()
//            val bundle = Bundle()
//            val intent = Intent(requireContext(), CalculationActivity::class.java)
//            bundle.putString("service",gridList2[i].title.toString())
//            intent.putExtras(bundle)
//            startActivity(intent)
        }

        gridList2 = ArrayList<GridHistoryModel>()
        gridList2 = gridList2 + GridHistoryModel("#Cuci2","12/05/2023","Setrika")
        gridList2 = gridList2 + GridHistoryModel("#Cuci4","12/05/2023","Setrika")
        gridAdapter = GridHistoryAdapter(gridList2,requireContext())
        gridDone.adapter = gridAdapter
        gridDoing.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i, l ->
            Toast.makeText(requireContext(),gridList2[i].entryCode,Toast.LENGTH_SHORT).show()
//            val bundle = Bundle()
//            val intent = Intent(requireContext(), CalculationActivity::class.java)
//            bundle.putString("service",gridList2[i].title.toString())
//            intent.putExtras(bundle)
//            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    companion object {
//        fun newInstance(bundle: Bundle): GalleryFragment {
//            val fragment = GalleryFragment()
//            fragment.arguments = bundle
//            return fragment
//        }
//    }
}