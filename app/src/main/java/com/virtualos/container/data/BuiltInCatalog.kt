package com.virtualos.container.data

/** Metadata only. A ROM is downloadable only after the operator supplies a verified mirror manifest. */
object BuiltInCatalog {
    val versions = listOf(
        Triple("Android 16",36,"APEX / Treble"), Triple("Android 15",35,"APEX / Treble"), Triple("Android 14",34,"APEX / Treble"),
        Triple("Android 13",33,"APEX / Treble"), Triple("Android 12L",32,"APEX / Treble"), Triple("Android 11",30,"Treble"),
        Triple("Android 10",29,"Treble"), Triple("Android 9 Pie",28,"Treble"), Triple("Android 8.1 Oreo",27,"Legacy"),
        Triple("Android 7.1 Nougat",25,"Legacy"), Triple("Android 6.0 Marshmallow",23,"Legacy"), Triple("Android 5.1 Lollipop",22,"Legacy"),
        Triple("Android 4.4 KitKat",19,"Legacy"), Triple("Android 4.2 Jelly Bean",17,"Legacy")
    )
}
