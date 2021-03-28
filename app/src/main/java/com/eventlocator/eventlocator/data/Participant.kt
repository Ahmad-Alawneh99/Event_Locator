package com.eventlocator.eventlocator.data

class Participant(var email: String, var firstName: String, var lastName: String, var password: String,
                  var rating: Double, var city: Int, var preferredEventCategories: List<Int>) {
    var id: Long = 0
    constructor(id: Long, email: String, firstName: String, lastName: String, rating: Double,
                city:Int, preferredEventCategories: List<Int>)
            :this(email, firstName, lastName, "", rating, city, preferredEventCategories){
                this.id = id
            }
}