package com.example.myfridgeapp

import java.io.Serializable

data class FridgeData(val name: String, val floor: Int, val fid: Int = 0) : Serializable