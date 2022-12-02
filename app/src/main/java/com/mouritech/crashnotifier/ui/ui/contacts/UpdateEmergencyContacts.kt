package com.mouritech.crashnotifier.UI.ui.contacts

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mouritech.crashnotifier.UI.adapters.ContactDetails
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.data.viewmodel.UpdateEmergencyContactsViewModel
import com.mouritech.crashnotifier.databinding.FragmentUpdateEmergencyContactsBinding
import com.mouritech.crashnotifier.utils.Utils


class UpdateEmergencyContacts : Fragment() {

    private var _binding: FragmentUpdateEmergencyContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EmergencyContactViewModel
    private lateinit var emergencyContactList: List<EmergencyContacts>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val updateEmergencyContactsViewModel =
            ViewModelProvider(this).get(UpdateEmergencyContactsViewModel::class.java)

        _binding = FragmentUpdateEmergencyContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setEmergencyContacts()

        val textView: TextView = binding.textSlideshow
        updateEmergencyContactsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.AddContacts.setOnClickListener {

            startActivity(Intent(requireActivity(),AddEmergencyContactFromPhoneContacts::class.java))
        }

        return root
    }

    private fun setEmergencyContacts() {
        progress = ProgressDialog(requireActivity())
        Utils.displayProgressBar(progress,"Fetching Emergency Contacts")
        emergencyContactList= ArrayList()

        activity.let { it ->
            viewModel = ViewModelProvider(it!!).get(EmergencyContactViewModel::class.java)
            val preferences = this.requireActivity().getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
            viewModel.getEmergencyContact2( Utils.mobileNumber(preferences))
            viewModel._emergencyContacts.observe(viewLifecycleOwner, Observer {data->
                emergencyContactList = viewModel._emergencyContacts.value!!
                binding.contactsRV.layoutManager = LinearLayoutManager(requireActivity())
                val adapter = ContactDetails(data)
                binding.contactsRV.adapter = adapter
                Utils.stopProgressBar(progress)
            })

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{
        lateinit var progress : ProgressDialog
    }

   /* fun onBackPressed() {

    }*/

}