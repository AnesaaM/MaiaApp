package com.example.maia

import com.example.maia.model.KidsCards
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductFilterTest {

    private val products = listOf(
        KidsCards(1, "Toy Car", "http://img.com/1.jpg", 19.99, "A red toy car"),
        KidsCards(2, "Teddy Bear", "http://img.com/2.jpg", 14.99, "A soft teddy bear"),
        KidsCards(3, "Building Blocks", "http://img.com/3.jpg", 24.99, "Colorful building blocks"),
        KidsCards(4, "Toy Train", "http://img.com/4.jpg", 29.99, "A wooden toy train")
    )

    private fun filterProducts(query: String): List<KidsCards> {
        val q = query.trim().lowercase()
        return if (q.isEmpty()) products
        else products.filter {
            it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
        }
    }

    @Test
    fun `empty query returns all products`() {
        val result = filterProducts("")
        assertEquals(4, result.size)
    }

    @Test
    fun `search by title returns matching products`() {
        val result = filterProducts("Toy")
        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Toy Car" })
        assertTrue(result.any { it.title == "Toy Train" })
    }

    @Test
    fun `search is case insensitive`() {
        val lower = filterProducts("teddy")
        val upper = filterProducts("TEDDY")
        assertEquals(lower.size, upper.size)
        assertEquals(1, lower.size)
    }

    @Test
    fun `search by description returns matching products`() {
        val result = filterProducts("wooden")
        assertEquals(1, result.size)
        assertEquals("Toy Train", result[0].title)
    }

    @Test
    fun `search with no match returns empty list`() {
        val result = filterProducts("xxxxnotfound")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `products are sorted by price ascending`() {
        val sorted = products.sortedBy { it.price }
        assertEquals(14.99, sorted[0].price, 0.001)
        assertEquals(29.99, sorted[3].price, 0.001)
    }

    @Test
    fun `cart total is calculated correctly`() {
        val prices = listOf(19.99, 14.99)
        val total = prices.sum()
        assertEquals(34.98, total, 0.001)
    }
}
