package com.example.readblogapp

 data class Users(var name: String="", var email_id: String="", var password: String="", var profile_image: String=""){
  constructor() : this("","","","")
 }
