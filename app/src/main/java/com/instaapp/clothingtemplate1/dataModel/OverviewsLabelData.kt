package com.instaapp.clothingtemplate1.dataModel


data class OverviewsLabelData(
    val brand: ArrayList<Brand>,
    val Size: ArrayList<SizeName>,
    val Color: ArrayList<ColorName>
)

data class Brand(
    val brand: String
)

data class SizeName(
    val size: String
)

data class ColorName(
    val color: String
)