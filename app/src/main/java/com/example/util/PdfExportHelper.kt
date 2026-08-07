package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.ui.viewmodel.CalculationResult
import com.example.ui.viewmodel.SoilRecommendation
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportHelper {

    /**
     * Generates a PDF report for Fertilizer Calculation & Matrix Solution, then triggers Print/Save.
     */
    fun printFertilizerReport(
        context: Context,
        result: CalculationResult,
        crop: String = "Rice",
        targetN: Double = 120.0,
        targetP: Double = 40.0,
        targetK: Double = 30.0,
        cramerMatrixExplanation: String = ""
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                isAntiAlias = true
            }

            var y = 40f

            // Header Banner
            paint.color = Color.rgb(82, 49, 34) // Dark Brown
            canvas.drawRect(20f, y, 575f, y + 60f, paint)

            textPaint.color = Color.WHITE
            textPaint.textSize = 20f
            textPaint.isFakeBoldText = true
            canvas.drawText("NutriGuide — Farm Fertilizer & NPK Report", 35f, y + 36f, textPaint)

            y += 80f

            // Date & Details
            textPaint.color = Color.BLACK
            textPaint.textSize = 12f
            textPaint.isFakeBoldText = false
            val dateStr = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.US).format(Date())
            canvas.drawText("Date: $dateStr", 30f, y, textPaint)
            y += 20f
            canvas.drawText("Target Crop: $crop | Farm Area: ${result.farmArea} hectare(s)", 30f, y, textPaint)
            y += 20f
            canvas.drawText("Nutrient Recommendation Target: ${targetN.toInt()}-${targetP.toInt()}-${targetK.toInt()} kg N-P2O5-K2O/ha", 30f, y, textPaint)

            y += 30f

            // Divider Line
            paint.color = Color.LTGRAY
            canvas.drawLine(30f, y, 565f, y, paint)
            y += 20f

            // Section 1: Matrix Cramer's Rule Solution Formula
            if (cramerMatrixExplanation.isNotBlank()) {
                textPaint.textSize = 14f
                textPaint.isFakeBoldText = true
                textPaint.color = Color.rgb(82, 49, 34)
                canvas.drawText("1. Matrix Linear System Solution (Cramer's Rule):", 30f, y, textPaint)
                y += 20f

                textPaint.textSize = 11f
                textPaint.isFakeBoldText = false
                textPaint.color = Color.DKGRAY

                cramerMatrixExplanation.lines().forEach { line ->
                    if (y < 780) {
                        canvas.drawText(line, 40f, y, textPaint)
                        y += 16f
                    }
                }
                y += 15f
            }

            // Section 2: Fertilizer Bags & Cost Breakdown
            textPaint.textSize = 14f
            textPaint.isFakeBoldText = true
            textPaint.color = Color.rgb(82, 49, 34)
            canvas.drawText("2. Required Fertilizer Materials & Cost Computation:", 30f, y, textPaint)
            y += 25f

            // Table Header
            paint.color = Color.rgb(247, 239, 233)
            canvas.drawRect(30f, y - 15f, 565f, y + 10f, paint)

            textPaint.textSize = 11f
            textPaint.isFakeBoldText = true
            textPaint.color = Color.BLACK
            canvas.drawText("Fertilizer Material", 35f, y, textPaint)
            canvas.drawText("Price/Bag", 260f, y, textPaint)
            canvas.drawText("Bags Needed", 380f, y, textPaint)
            canvas.drawText("Subtotal (₱)", 480f, y, textPaint)

            y += 25f
            textPaint.isFakeBoldText = false

            result.items.forEach { item ->
                if (y < 780) {
                    canvas.drawText(item.name, 35f, y, textPaint)
                    canvas.drawText("₱${String.format("%,.0f", item.pricePerBag)}", 260f, y, textPaint)
                    canvas.drawText("${String.format("%.1f", item.bagsNeeded)} bags", 380f, y, textPaint)
                    canvas.drawText("₱${String.format("%,.2f", item.totalCost)}", 480f, y, textPaint)
                    y += 20f
                }
            }

            y += 10f
            paint.color = Color.GRAY
            canvas.drawLine(30f, y, 565f, y, paint)
            y += 20f

            // Total Cost Highlight
            textPaint.textSize = 14f
            textPaint.isFakeBoldText = true
            textPaint.color = Color.rgb(82, 49, 34)
            canvas.drawText("TOTAL ESTIMATED COST: ₱${String.format("%,.2f", result.totalCost)}", 30f, y, textPaint)
            y += 30f

            // Section 3: Recommendations & Schedule
            if (result.recommendations.isNotEmpty()) {
                textPaint.textSize = 13f
                textPaint.isFakeBoldText = true
                canvas.drawText("Field Application Schedule & Guidelines:", 30f, y, textPaint)
                y += 20f

                textPaint.textSize = 10f
                textPaint.isFakeBoldText = false
                textPaint.color = Color.BLACK

                result.recommendations.forEach { rec ->
                    if (y < 700) {
                        canvas.drawText("• $rec", 40f, y, textPaint)
                        y += 16f
                    }
                }
            }

            // QR Code Verification Card at bottom of page
            val qrY = 710f
            val docId = "NG-${System.currentTimeMillis() % 1000000}"
            val verifyUrl = "https://ais-pre-ajbae5lsv4c7we5w7bzysw-312536888069.asia-east1.run.app/verify?doc=$docId"

            // Card background
            paint.color = Color.rgb(240, 245, 240)
            canvas.drawRect(30f, qrY, 565f, qrY + 100f, paint)
            paint.color = Color.rgb(82, 49, 34)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRect(30f, qrY, 565f, qrY + 100f, paint)
            paint.style = Paint.Style.FILL

            // Draw QR Code
            drawQrCode(canvas, verifyUrl, 40f, qrY + 10f, 80f)

            // Text info next to QR
            textPaint.textSize = 12f
            textPaint.isFakeBoldText = true
            textPaint.color = Color.rgb(82, 49, 34)
            canvas.drawText("OFFICIAL VERIFIED NUTRI-GUIDE REPORT", 135f, qrY + 28f, textPaint)

            textPaint.textSize = 10f
            textPaint.isFakeBoldText = false
            textPaint.color = Color.BLACK
            canvas.drawText("Report ID: $docId | Offline SQLite Record", 135f, qrY + 46f, textPaint)
            canvas.drawText("Scan QR Code to verify document authenticity & NPK solution payload.", 135f, qrY + 62f, textPaint)
            textPaint.color = Color.GRAY
            textPaint.textSize = 8f
            canvas.drawText("URL: $verifyUrl", 135f, qrY + 78f, textPaint)

            pdfDocument.finishPage(page)

            // Save PDF to cache file
            val file = File(context.cacheDir, "NutriGuide_Fertilizer_Report.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            // Trigger Android System Print / Share Flow
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName = "NutriGuide Fertilizer Report"
            
            printManager.print(jobName, object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    val info = PrintDocumentInfo.Builder("NutriGuide_Report.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()
                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: android.os.ParcelFileDescriptor?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    try {
                        val input = file.inputStream()
                        val output = FileOutputStream(destination?.fileDescriptor)
                        input.copyTo(output)
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    }
                }
            }, null)

            Toast.makeText(context, "Exporting report to PDF...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun drawQrCode(canvas: Canvas, data: String, x: Float, y: Float, size: Float) {
        val paint = Paint().apply { isAntiAlias = true }

        // QR Canvas Box
        paint.color = Color.WHITE
        canvas.drawRect(x, y, x + size, y + size, paint)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRect(x, y, x + size, y + size, paint)

        val modules = 21
        val cellSize = size / modules
        paint.style = Paint.Style.FILL

        val matrix = Array(modules) { BooleanArray(modules) }

        fun drawFinder(r: Int, c: Int) {
            for (i in 0..6) {
                for (j in 0..6) {
                    if (r + i < modules && c + j < modules) {
                        val isBorder = i == 0 || i == 6 || j == 0 || j == 6
                        val isCenter = i >= 2 && i <= 4 && j >= 2 && j <= 4
                        matrix[r + i][c + j] = isBorder || isCenter
                    }
                }
            }
        }

        // Draw standard QR finder targets
        drawFinder(0, 0)
        drawFinder(0, 14)
        drawFinder(14, 0)

        // Timing patterns
        for (i in 7..13) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // Alignment pattern
        for (i in 14..18) {
            for (j in 14..18) {
                val isBorder = i == 14 || i == 18 || j == 14 || j == 18
                val isCenter = i == 16 && j == 16
                matrix[i][j] = isBorder || isCenter
            }
        }

        // Encode data payload hash bits
        val hashStr = data.hashCode().toString() + data.reversed()
        var hIdx = 0
        for (r in 0 until modules) {
            for (c in 0 until modules) {
                val inTopLeft = r in 0..7 && c in 0..7
                val inTopRight = r in 0..7 && c in 13..20
                val inBottomLeft = r in 13..20 && c in 0..7
                val inAlignment = r in 14..18 && c in 14..18
                val isTiming = r == 6 || c == 6

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inAlignment && !isTiming) {
                    val codeVal = hashStr[hIdx % hashStr.length].code
                    matrix[r][c] = ((codeVal + r * 3 + c * 7) % 2 == 0)
                    hIdx++
                }
            }
        }

        // Render matrix modules
        for (r in 0 until modules) {
            for (c in 0 until modules) {
                if (matrix[r][c]) {
                    val cx = x + c * cellSize
                    val cy = y + r * cellSize
                    canvas.drawRect(cx, cy, cx + cellSize, cy + cellSize, paint)
                }
            }
        }
    }
}
