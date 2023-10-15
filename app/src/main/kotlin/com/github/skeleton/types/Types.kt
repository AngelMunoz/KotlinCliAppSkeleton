package com.github.skeleton.types


data class ShowParams(val path: String, val recursive: Boolean = false)

interface IFileSystem {
    fun listItems(path: String): List<String>
    fun walkDown(path: String): List<String>
}
