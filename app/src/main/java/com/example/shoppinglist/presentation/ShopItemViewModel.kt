package com.example.shoppinglist.presentation

import android.app.Application
import android.net.ParseException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.AddShopItemUseCase
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopItemUseCase
import com.example.shoppinglist.domain.ShopItem
import kotlinx.coroutines.launch

class ShopItemViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ShopListRepositoryImpl(application)

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    private val _inputErrorName = MutableLiveData<Boolean>()
    val inputErrorName: LiveData<Boolean> = _inputErrorName

    private val _inputErrorCount = MutableLiveData<Boolean>()
    val inputErrorCount: LiveData<Boolean> = _inputErrorCount

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem> = _shopItem

    private val _endEditing = MutableLiveData<Unit>()
    val endEditing: LiveData<Unit> = _endEditing

    fun getShopItem(shopItemId: Int) {
        viewModelScope.launch {
            val item = getShopItemUseCase.getShopItem(shopItemId)
            _shopItem.postValue(item)
        }
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValue = validateInput(name, count)
        if (fieldValue) {
            viewModelScope.launch {
                val shopItem = ShopItem(name, count, true)
                addShopItemUseCase.addShopItem(shopItem)
                finishWork()
            }
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValue = validateInput(name, count)
        if (fieldValue) {
            _shopItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(name = name, count = count)
                    editShopItemUseCase.editShopItem(item)
                    finishWork()
                }
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: ParseException) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _inputErrorName.value = true
            result = false
        }
        if (count <= 0) {
            _inputErrorCount.value = true
            result = false
        }
        return result

    }

    fun resetErrorInputName() {
        _inputErrorName.value = false
    }

    fun resetInputErrorCount() {
        _inputErrorCount.value = false
    }

    private fun finishWork() {
        _endEditing.postValue(Unit)
    }
}