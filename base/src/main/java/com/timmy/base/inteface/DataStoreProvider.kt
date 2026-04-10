package com.timmy.base.inteface

interface DataStoreProvider {
    fun getToken(): String

//    fun getAccountData(): CommunityAccountData
//
//    fun setLoginInfo(loginInfo: ResponseCommunityLoginInfo)

    fun getStoreId(): String

    fun getETag(url: String): String

    fun setETag(url: String, eTag: String)

}
