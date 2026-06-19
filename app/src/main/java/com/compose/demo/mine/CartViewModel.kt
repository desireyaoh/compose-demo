package com.compose.demo.mine

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartItem(val id: Int, val name: String, val quantity: Int)

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow(
        listOf(
            CartItem(id = 1, name = "可乐", quantity = 1),
            CartItem(id = 2, name = "薯片", quantity = 2),
            CartItem(id = 3, name = "矿泉水", quantity = 0)
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun increment(id: Int) {
        _cartItems.update { items ->
            items.map { if (it.id == id) it.copy(quantity = it.quantity + 1) else it }
        }
    }

    fun decrement(id: Int) {
        _cartItems.update { items ->
            items.map { if (it.id == id) it.copy(quantity = maxOf(0, it.quantity - 1)) else it }
        }
    }
}
