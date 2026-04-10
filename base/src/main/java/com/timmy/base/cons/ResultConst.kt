package com.timmy.base.cons

sealed class ResultConst {
    object Idle : ResultConst()
    object Complete : ResultConst()
    data class Error(val msg:String) : ResultConst()
    data class Success(val Type:String) : ResultConst()
}