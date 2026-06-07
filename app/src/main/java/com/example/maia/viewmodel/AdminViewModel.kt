package com.example.maia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.KidsCards
import com.example.maia.model.MenCard
import com.example.maia.model.WomenCard
import com.example.maia.model.admin.ChangeRoleRequest
import com.example.maia.model.admin.CreateStaffRequest
import com.example.maia.model.admin.Role
import com.example.maia.model.admin.UpdateUserRequest
import com.example.maia.model.admin.User
import com.example.maia.model.men.MenCardRequest
import com.example.maia.model.men.MenCategory
import com.example.maia.model.women.SetDiscountRequest
import com.example.maia.model.women.WomenCardRequest
import com.example.maia.model.women.WomenCategory
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    var users by mutableStateOf<List<User>>(emptyList()); private set
    var customers by mutableStateOf<List<User>>(emptyList()); private set
    var roles by mutableStateOf<List<Role>>(emptyList()); private set
    var womenCards by mutableStateOf<List<WomenCard>>(emptyList()); private set
    var menCards by mutableStateOf<List<MenCard>>(emptyList()); private set
    var kidsCards by mutableStateOf<List<KidsCards>>(emptyList()); private set
    var womenCategories by mutableStateOf<List<WomenCategory>>(emptyList()); private set
    var menCategories by mutableStateOf<List<MenCategory>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                // Users / customers / staff — show actual error so it can be diagnosed
                try {
                    users     = RetrofitInstance.adminApi.getAllUsers()
                    customers = RetrofitInstance.adminApi.getCustomers()
                } catch (e: Exception) {
                    error = "Users API: ${e.message}"
                }
                try { roles = RetrofitInstance.adminApi.getRoles() } catch (_: Exception) {}

                // Women products
                try {
                    womenCards = RetrofitInstance.womenManagerApi.getAllCards()
                } catch (_: Exception) {
                    womenCards = emptyList()
                }
                // Men products
                try {
                    menCards = RetrofitInstance.menManagerApi.getAllCards()
                } catch (_: Exception) {
                    menCards = emptyList()
                }
                // Kids — backend has an issue; load silently, show empty on failure
                try {
                    kidsCards = RetrofitInstance.kidsApi.getKidsCards()
                } catch (_: Exception) {
                    kidsCards = emptyList()
                }

                // Categories (non-critical)
                try { womenCategories = RetrofitInstance.womenManagerApi.getCategories() } catch (_: Exception) {}
                try { menCategories   = RetrofitInstance.menManagerApi.getCategories()   } catch (_: Exception) {}
            } finally {
                isLoading = false
            }
        }
    }

    // Staff management
    fun createStaff(req: CreateStaffRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.adminApi.createStaff(req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateUser(id: Int, req: UpdateUserRequest, newRoleType: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.adminApi.updateUser(id, req)
                val role = roles.find { it.roleType == newRoleType }
                if (role != null) RetrofitInstance.adminApi.changeRole(ChangeRoleRequest(id, role.roleID))
                loadAll(); onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteUser(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.adminApi.deleteUser(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun toggleStatus(user: User, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.adminApi.setUserStatus(user.userID, !user.isActive); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    // Women product management
    fun createWomenCard(req: WomenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.createCard(req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateWomenCard(id: Int, req: WomenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.updateCard(id, req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteWomenCard(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.deleteCard(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    // Men product management
    fun createMenCard(req: MenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.createCard(req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateMenCard(id: Int, req: MenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.updateCard(id, req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteMenCard(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.deleteCard(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    // Kids product management
    fun createKidsCard(title: String, price: Double, imageUrl: String, description: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.kidsApi.createKidsCard(mapOf("title" to title, "price" to price, "imageUrl" to imageUrl, "description" to description))
                loadAll(); onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateKidsCard(id: Int, title: String, price: Double, imageUrl: String, description: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.kidsApi.updateKidsCard(id, mapOf("title" to title, "price" to price, "imageUrl" to imageUrl, "description" to description))
                loadAll(); onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteKidsCard(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.deleteKidsCard(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    // Sales discount management
    fun setWomenDiscount(id: Int, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.setDiscount(id, SetDiscountRequest(pct)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun setMenDiscount(card: MenCard, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.menManagerApi.updateCard(card.id, MenCardRequest(
                    title = card.title, description = card.description,
                    price = card.price, imageUrl = card.imageUrl,
                    menCategoryId = card.menCategoryId, color = card.color,
                    discountPercent = pct
                ))
                loadAll(); onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun setKidsDiscount(id: Int, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.setKidsDiscount(id, mapOf("discountPercent" to pct)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    val staff get() = users.filter { it.roleType != "Customer" }
}
