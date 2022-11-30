package com.mouritech.crashnotifier.UI.ui.health_details

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.R.id.bloodGroupInput
import com.mouritech.crashnotifier.R.id.healthDataInput
import com.mouritech.crashnotifier.data.viewmodel.UpdateHealthDetailsViewModel
import com.mouritech.crashnotifier.databinding.FragmentUpdateHealthDataBinding
import com.mouritech.crashnotifier.utils.Utils


class UpdateHealthDetails : Fragment() {

    private var _binding: FragmentUpdateHealthDataBinding? = null
    private val binding get() = _binding!!
    lateinit var  nearEmergencyContactsViewModel : UpdateHealthDetailsViewModel
    lateinit var mobileNumber : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         nearEmergencyContactsViewModel =
            ViewModelProvider(this).get(UpdateHealthDetailsViewModel::class.java)

        _binding = FragmentUpdateHealthDataBinding.inflate(inflater, container, false)
        progress = ProgressDialog(requireActivity())
        Utils.displayProgressBar(progress,"Fetching Data")
        val preferences = this.requireActivity().getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        mobileNumber = preferences.getString("login_mobile_number", "").toString()
        nearEmergencyContactsViewModel.getDetails(mobileNumber)

        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        nearEmergencyContactsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        nearEmergencyContactsViewModel.bloodGroup.observe(viewLifecycleOwner){
            binding.bloodGroup.text = it.toString()
        }
        nearEmergencyContactsViewModel.healthData.observe(viewLifecycleOwner){
            if (it.toString().isEmpty()){
                binding.healthData.text = "No Details are added."
            }
            else{
                binding.healthData.text = it.toString()
            }

        }

        binding.hdEdit.setOnClickListener {
            showEditDataPopup("Health Data")
        }
        binding.bgEdit.setOnClickListener {
            showEditDataPopup("Blood Group")
        }
        binding.update.setOnClickListener {
            Utils.displayProgressBar(progress,"Updating Data")
            nearEmergencyContactsViewModel.editData(mobileNumber)
        }

        return root
    }

    private fun showEditDataPopup(editField: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(editField)
        val customLayout: View = layoutInflater.inflate(com.mouritech.crashnotifier.R.layout.popup_edit_health_data,
                null)
        builder.setView(customLayout)

        val bloodGroupInput : TextInputLayout = customLayout.findViewById<TextInputLayout>(bloodGroupInput)
        val healthDetailsInput : TextInputLayout= customLayout.findViewById<TextInputLayout>(healthDataInput)
        var bloodGroupSpinner :Spinner = customLayout.findViewById<Spinner>(R.id.bloodGroupSpinner)
        var bloodGroupSpinnerAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.blood_groups,
            R.layout.dropdown_item
        )
        bloodGroupSpinnerAdapter.setDropDownViewResource(R.layout.dropdown_item)
        bloodGroupSpinner.adapter = bloodGroupSpinnerAdapter

        if (editField == "Health Data"){
            bloodGroupInput.visibility = View.GONE
            healthDetailsInput.visibility = View.VISIBLE
        }
        else if (editField == "Blood Group"){
            bloodGroupInput.visibility = View.VISIBLE
            healthDetailsInput.visibility = View.GONE
        }

        builder.setPositiveButton(
                "Save",
                DialogInterface.OnClickListener { dialog, which ->
                    if (editField == "Health Data"){
                        val healthDetails:TextInputEditText = customLayout.findViewById<TextInputEditText>(R.id.healthData)
                        nearEmergencyContactsViewModel._healthData.value = healthDetails.text.toString()
                    }
                    else if (editField == "Blood Group"){

                        if (bloodGroupSpinner.selectedItem.toString() == "Select Blood Group"){
                            Toast.makeText(requireActivity(),"Please select blood group",Toast.LENGTH_SHORT).show()
                        }
                        else {
                            nearEmergencyContactsViewModel._bloodGroup.value = bloodGroupSpinner.selectedItem.toString()
                        }
                    }
                })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{
        lateinit var progress : ProgressDialog
    }
}