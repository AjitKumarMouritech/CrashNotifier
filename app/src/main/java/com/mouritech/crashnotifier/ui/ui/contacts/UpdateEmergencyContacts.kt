package com.mouritech.crashnotifier.ui.ui.contacts


import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.data.viewmodel.UpdateEmergencyContactsViewModel
import com.mouritech.crashnotifier.databinding.FragmentUpdateEmergencyContactsBinding
import com.mouritech.crashnotifier.ui.adapters.ContactDetails
import com.mouritech.crashnotifier.utils.Utils


class UpdateEmergencyContacts : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val updateEmergencyContactsViewModel =
            ViewModelProvider(this).get(UpdateEmergencyContactsViewModel::class.java)

        _binding = FragmentUpdateEmergencyContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        preferences = requireActivity().getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        setEmergencyContacts()

        progress = ProgressDialog(requireActivity())

        binding.contactsRV.layoutManager = LinearLayoutManager(requireActivity())
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
        var progress = ProgressDialog(requireActivity())
        Utils.displayProgressBar(progress,"Fetching Emergency Contacts")
        emergencyContactList= ArrayList()

        activity.let { it ->
            viewModel = ViewModelProvider(it!!).get(EmergencyContactViewModel::class.java)
            val preferences = this.requireActivity().getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
            viewModel.getEmergencyContact2( Utils.getUserID(preferences),"")
            viewModel._emergencyContacts.observe(viewLifecycleOwner, Observer {data->
                emergencyContactList = viewModel._emergencyContacts.value!!
                binding.contactsRV.layoutManager = LinearLayoutManager(requireActivity())
                val adapter = ContactDetails(data,"update_emergency_contacts")
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
        lateinit var preferences :SharedPreferences
        private lateinit var emergencyContactList: List<EmergencyContacts>
        private lateinit var viewModel: EmergencyContactViewModel
        val binding get() = _binding!!
        private var _binding: FragmentUpdateEmergencyContactsBinding? = null

        fun setData(){
            emergencyContactList = viewModel._emergencyContacts.value!!
            val adapter = ContactDetails(emergencyContactList,"update_emergency_contacts")
            binding.contactsRV.adapter = adapter
        }

        fun deleteEmergencyContacts(position:Int,number:String){

            Utils.displayProgressBar(progress,"Deleting")
            FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference().child("emergency_contact_details")
            myRef.orderByChild("user_mobile_number").equalTo(Utils.mobileNumber(preferences))
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (each_item_snapshot in dataSnapshot.children) {
                             if (number == each_item_snapshot.child("emergency_contact_number").value.toString()){
                                 each_item_snapshot.ref.removeValue()
                             }
                        }
                        viewModel.getEmergencyContact2(Utils.getUserID(preferences),"delete")
                        Utils.stopProgressBar(progress)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Update view model", error.toString())
                    }
                })
        }
    }

}