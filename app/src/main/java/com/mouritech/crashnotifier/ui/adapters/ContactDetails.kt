package com.mouritech.crashnotifier.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.ui.AddEmergencyContact
import com.mouritech.crashnotifier.ui.ui.contacts.AddEmergencyContactFromPhoneContacts
import com.mouritech.crashnotifier.ui.ui.contacts.UpdateEmergencyContacts
import com.mouritech.crashnotifier.utils.Utils

class ContactDetails(
    private val mList: List<EmergencyContacts>,
    val deleteControl :String
) : RecyclerView.Adapter<ContactDetails.ViewHolder>() {

    lateinit var viewModel: EmergencyContactViewModel

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val mobileNumber: TextView = itemView.findViewById(R.id.number)
        val delete : ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       //viewModel = ViewModelProvider(context)[EmergencyContactViewModel::class.java]
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contacts_list_data, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.name.text  = ItemsViewModel.emergency_contact_name
        holder.mobileNumber.text = ItemsViewModel.emergency_contact_number

        when (deleteControl) {
            "signup_add_emergency_contacts" -> {
                holder.delete.visibility = View.VISIBLE
            }
            "update_emergency_contacts" -> {
                holder.delete.visibility = View.VISIBLE
            }
            "add_emergency_contacts_phone_book" -> {
                holder.delete.visibility = View.VISIBLE
            }
        }

        holder.delete.setOnClickListener {
            mList.drop(position)
            when (deleteControl) {
                "signup_add_emergency_contacts" -> {
                    AddEmergencyContact.remove(position)
                    notifyDataSetChanged()
                }
                "update_emergency_contacts" -> {
                    UpdateEmergencyContacts.deleteEmergencyContacts(position,mList[position].emergency_contact_number.toString())
                    notifyDataSetChanged()
                }
                "add_emergency_contacts_phone_book" -> {
                    AddEmergencyContactFromPhoneContacts.remove(position)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}