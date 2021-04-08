package com.nima.bluetoothchatapp

import kotlin.random.Random

class RandomUIDGenerator() {
    fun generate() : String{
        return Random.nextInt(1000,9999).toString()
    }
    fun generate(number :Int) : List<String> {
        return List(number) { Random.nextInt(1000, 9999).toString() }
    }
}