package ru.cobalt42.auth.model.user

import ru.cobalt42.auth.model.dictionary.Target

data class ImageData(
    val fileData: String = "",
    val originName: String = "",
    val target: Target = Target(),
    val uname: String = ""
)