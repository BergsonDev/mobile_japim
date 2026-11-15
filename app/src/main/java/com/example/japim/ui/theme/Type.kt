
package com.example.japim.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.japim.R

// Fonte M PLUS 2
val MPlus2 = FontFamily(
    Font(R.font.mplus2_regular, FontWeight.Normal),
    Font(R.font.mplus2_medium, FontWeight.Medium),
    Font(R.font.mplus2_bold, FontWeight.Bold)
)

// Tipografia do aplicativo
val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = MPlus2,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = MPlus2,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
)
