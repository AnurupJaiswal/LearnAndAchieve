package com.anurupjaiswal.learnandachieve.model

data class OrderHistoryResponse(
    val message: String,                // "Success"
    val data: List<Order>,              // List of orders
    val availableDataCount: Int         // The total number of orders available
)

data class Order(
        val order_id: String,               // "678634c98a60f3272586eea0"
    val orderId: String,                // "ORD-20250114-60181"
    val packageName: String,            // "Class 12th Package Test"
    val date: String,                   // "14 January, 2025"
    val transaction_id: String,         // "TXN-20250114-b62844d9d50de0da"
    val amount: String                  // "4000.00"
)