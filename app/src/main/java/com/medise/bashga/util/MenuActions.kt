package com.medise.bashga.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class MenuActions(
    @StringRes val label:Int,
    @DrawableRes val icon:Int
){

}
