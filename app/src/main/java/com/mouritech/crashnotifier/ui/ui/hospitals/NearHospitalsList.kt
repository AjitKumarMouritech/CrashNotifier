package com.mouritech.crashnotifier.UI.ui.hospitals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mouritech.crashnotifier.data.viewmodel.NearHospitalsListViewModel
import com.mouritech.crashnotifier.databinding.FragmentHospitalsListBinding

class NearHospitalsList : Fragment() {

    private var _binding: FragmentHospitalsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nearHospitalsListViewModel =
            ViewModelProvider(this).get(NearHospitalsListViewModel::class.java)

        _binding = FragmentHospitalsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        nearHospitalsListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}