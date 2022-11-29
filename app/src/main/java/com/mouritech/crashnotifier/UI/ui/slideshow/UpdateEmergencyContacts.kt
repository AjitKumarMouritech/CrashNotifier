package com.mouritech.crashnotifier.UI.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mouritech.crashnotifier.data.viewmodel.UpdateEmergencyContactsViewModel
import com.mouritech.crashnotifier.databinding.FragmentUpdateEmergencyContactsBinding

class UpdateEmergencyContacts : Fragment() {

    private var _binding: FragmentUpdateEmergencyContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val updateEmergencyContactsViewModel =
            ViewModelProvider(this).get(UpdateEmergencyContactsViewModel::class.java)

        _binding = FragmentUpdateEmergencyContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        updateEmergencyContactsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}