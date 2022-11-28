package com.mouritech.crashnotifier.data.model

class Signup{

    lateinit var userName:String
    lateinit var mobileNumber:String
    lateinit var gender:String
    lateinit var dob:String
   // lateinit var contacts:MutableMap<String,String>
    lateinit var bloodGroup:String
    lateinit var healthData:String

    //Default constructor required for calls to
    //DataSnapshot.getValue(User.class)
    constructor(){

    }

    constructor(userName:String,mobileNumber:String,gender:String,dob:String,bloodGroup:String,healthData:String/*,contacts:MutableMap<String,String>*/){
        this.userName=userName
        this.mobileNumber=mobileNumber
        this.gender=gender
        this.dob=dob
        this.bloodGroup=bloodGroup
        this.healthData=healthData
        //this.contacts=contacts
    }
}