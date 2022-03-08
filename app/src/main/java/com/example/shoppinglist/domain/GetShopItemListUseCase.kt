package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

class GetShopItemListUseCase(private val shopListRepository: ShopListRepository) {
    fun getShopItemList(): LiveData<List<ShopItem>> {
        return shopListRepository.getShopItemList()
    }
}