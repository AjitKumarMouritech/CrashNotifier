package com.mouritech.crashnotifier.UI.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mouritech.crashnotifier.data.viewmodel.NearEmergencyContactsViewModel
import com.mouritech.crashnotifier.databinding.FragmentNearEmergencyContactBinding

class NearEmergencyContacts : Fragment() {

    private var _binding: FragmentNearEmergencyContactBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nearEmergencyContactsViewModel =
            ViewModelProvider(this).get(NearEmergencyContactsViewModel::class.java)

        _binding = FragmentNearEmergencyContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        nearEmergencyContactsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}