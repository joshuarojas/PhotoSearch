package com.joshrojas.photosearch.view.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val sdf = SimpleDateFormat("MMM dd yyyy", Locale.US)
fun Date.format(): String = sdf.format(this)