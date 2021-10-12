package com.example.mycloverpayment.customercarddetails.model.createordermodel

data class CreateCustomer(
    val cards: List<Card>,
    val emailAddresses: List<EmailAddresse>,
    val firstName: String,
    val lastName: String,
    val phoneNumbers: List<PhoneNumber>
)