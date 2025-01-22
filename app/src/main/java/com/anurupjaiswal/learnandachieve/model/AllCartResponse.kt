package com.anurupjaiswal.learnandachieve.model

class AllCartResponse {
    var message: String? = null
    var cartCount: Int = 0
    var cartList: List<CartItem>? = null
    var summary: CartSummary? = null
}
class CartItem {
    var cart_id: String? = null
    var package_id: String? = null
    var packageName: String? = null
    var actualPrice: String? = null
    var discountedPrice: String? = null
    var discountCoordinator: String? = null
    var mainImage: String? = null
    var user_id: String? = null
}
class CartSummary {
    var subTotal: String? = null
    var discountAmt: String? = null
    var grandTotalCoordinator: String? = null
    var grandTotal: String? = null
}
