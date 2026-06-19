package com.compose.demo.mine

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartViewModelTest {

    private lateinit var viewModel: CartViewModel

    @Before
    fun setUp() {
        viewModel = CartViewModel()
    }

    @Test
    fun initialState_hasThreeItems() {
        assertEquals(3, viewModel.cartItems.value.size)
    }

    @Test
    fun increment_increasesQuantityByOne() {
        viewModel.increment(1)
        val item = viewModel.cartItems.value.first { it.id == 1 }
        assertEquals(2, item.quantity)
    }

    @Test
    fun decrement_decreasesQuantityByOne() {
        viewModel.increment(1) // 初始 1 → 2
        viewModel.decrement(1) // 2 → 1
        val item = viewModel.cartItems.value.first { it.id == 1 }
        assertEquals(1, item.quantity)
    }

    @Test
    fun decrement_doesNotGoBelowZero() {
        viewModel.decrement(3) // 初始数量为 0
        val item = viewModel.cartItems.value.first { it.id == 3 }
        assertEquals(0, item.quantity)
    }

    @Test
    fun increment_doesNotAffectOtherItems() {
        viewModel.increment(1)
        val unchanged = viewModel.cartItems.value.first { it.id == 2 }
        assertEquals(2, unchanged.quantity)
    }
}
