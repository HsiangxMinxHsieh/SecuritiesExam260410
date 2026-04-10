package com.timmy.datastorelibs.di

import com.timmy.base.inteface.DataStoreProvider
import com.timmy.datastorelibs.repo.DataStoreRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreProvider @Inject constructor(
    private val repository: DataStoreRepository
) : DataStoreProvider {
    override fun getToken(): String {
        return ""
//        return repository.responseCommunityLoginInfo.token ?: ""
    }

    override fun getStoreId(): String {
        return ""
//        return repository.communityAccountData.storeId
    }

    override fun getETag(url: String): String {
        return ""
    }

    override fun setETag(url: String, eTag: String) {
        runBlocking { }
    }


}