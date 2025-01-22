package com.anurupjaiswal.learnandachieve.model

import com.google.gson.annotations.SerializedName

class CartResponse {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("cartData")
    var cartData: CartData? = null
}

class CartData {
    var cart_id: String? = null

    var user_id: String? = null

    var package_id: String? = null

    var packageName: String? = null

    var actualPrice: Int = 0

    var discountedPrice: Int = 0

    var discountCoordinator: Int = 0

    var mainImage: String? = null

    @SerializedName("is_active")
    var is_active: Boolean = false

    @SerializedName("is_deleted")
    var is_deleted: Boolean = false

    @SerializedName("created_date")
    var created_date: String? = null

    @SerializedName("updated_date")
    var updated_date: String? = null

    @SerializedName("__v")
    var __v: Int = 0

    @SerializedName("cartCount")
    var cartCount: Int = 0
}
