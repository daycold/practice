package com.practice.practice.price

open class Price(val originPrice: Int) {
    @Transient
    private var currentPrice: Int = originPrice

    val price: Int
        get() = currentPrice

    @Transient
    private val discountList: MutableList<Discount> = mutableListOf()

    @Transient
    private val uselessDiscountList: MutableList<Discount> = mutableListOf()

    val discounts: List<Discount>
        get() = discountList.toList()

    val uselessDiscounts: List<Discount>
        get() = uselessDiscountList.toList()

    fun addDiscount(discount: Discount) {
        if (isDiscountUseful(discount))
            discountList.add(discount)
    }

    open fun isDiscountUseful(discount: Discount): Boolean {
        return true
    }

    fun sortDiscount() {
        discountList.sort()
    }

    fun discount() {
        if (discountList.isEmpty()) return
        synchronized(discountList) {
            val sortedDiscounts = discountList.sorted()
            var i = 0
            val size = discountList.size
            while (i < size) {
                val discount = sortedDiscounts[i]
                i++
                val discounts = if (i == size) emptyList() else sortedDiscounts.subList(i, size)
                currentPrice = discount.handleDiscount(currentPrice, discounts)
            }
        }
    }
}

interface Discount : Comparable<Discount>{
    val value: Int
    val discountType: String
    val discountId: String

    fun handleDiscount(price: Int, nextDiscounts: List<Discount>): Int
}