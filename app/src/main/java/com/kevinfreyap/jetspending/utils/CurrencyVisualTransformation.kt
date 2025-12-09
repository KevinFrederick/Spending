package com.kevinfreyap.jetspending.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyVisualTransformation (
    currencyCode: AppCurrency
): VisualTransformation {

    private val numberFormat: DecimalFormat
    private val groupingChar: Char
    private val decimalChar: Char

    init {
        val locale = CurrencyUiFormatter.getLocale(currencyCode)

        val symbols = DecimalFormatSymbols(locale)
        groupingChar = symbols.groupingSeparator
        decimalChar = symbols.decimalSeparator

        numberFormat = (DecimalFormat.getNumberInstance(locale) as DecimalFormat).apply {
            isParseBigDecimal = true
            applyPattern("#,##0.##")
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val parts = raw.split('.')
        val intPart = parts[0]
        val fractionPart = if (parts.size > 1) parts[1] else null

        val formattedInt = try {
            if (intPart.isNotEmpty() && intPart != "-") {
                numberFormat.format(intPart.toBigInteger())
            } else {
                intPart
            }
        } catch (_: Exception) {
            intPart
        }

        val formatted = if (fractionPart != null && fractionPart != "00") {
            "$formattedInt.$fractionPart"
        } else {
            formattedInt
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (raw.isEmpty()) return 0
                if (offset >= raw.length) return formatted.length

                val inputsBefore = raw.take(offset).count { it.isDigit() || it == '.' }

                var outputIdx = 0
                var inputsFound = 0
                while (outputIdx < formatted.length && inputsFound < inputsBefore){
                    if (formatted[outputIdx].isDigit() || formatted[outputIdx] == decimalChar) {
                        inputsFound++
                    }
                    outputIdx++
                }
                return outputIdx
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (formatted.isEmpty()) return 0
                if (offset >= formatted.length) return raw.length

                val outputsBefore = formatted.take(offset).count { it.isDigit() || it == decimalChar }

                var inputIdx = 0
                var outputsFound = 0
                while (inputIdx < raw.length && outputsFound < outputsBefore) {
                    if (raw[inputIdx].isDigit() || raw[inputIdx] == '.'){
                        outputsFound++
                    }
                    inputIdx++
                }
                return inputIdx
            }

        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}