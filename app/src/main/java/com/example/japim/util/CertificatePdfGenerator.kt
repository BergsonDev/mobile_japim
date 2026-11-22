package com.example.japim.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.japim.graphql.MyCertificatesQuery
import java.io.OutputStream

object CertificatePdfGenerator {

    fun generate(
        certificate: MyCertificatesQuery.MyCertificate,
        outputStream: OutputStream
    ) {
        val document = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo
                .Builder(842, 595, 1)
                .create()

            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Background
            canvas.drawColor(Color.WHITE)

            // External border
            paint.color = Color.rgb(198, 161, 61)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f

            canvas.drawRect(20f, 20f, 822f, 575f, paint)

            // Internal border
            paint.strokeWidth = 1f

            canvas.drawRect(30f, 30f, 812f, 565f, paint)

            // Restore text style
            paint.style = Paint.Style.FILL

            drawCenteredText(
                canvas, paint,
                "JAPIM",
                90f, 30f, true
            )

            drawCenteredText(
                canvas, paint,
                "CERTIFICADO",
                155f, 42f, true
            )

            drawCenteredText(
                canvas, paint,
                "Certificamos que",
                205f, 18f
            )

            drawParagraph(
                canvas,
                certificate.user.name,
                230f,
                30f,
                true
            )

            val description = """
                Participou do evento ${certificate.event.name},
                realizado no período de
                ${certificate.event.date_start} a
                ${certificate.event.date_end},
                com carga horária total de
                ${certificate.event.workload} horas.
            """.trimIndent().replace("\n", " ")

            drawParagraph(
                canvas,
                description,
                340f,
                18f
            )

            drawCenteredText(
                canvas, paint,
                "Data de emissão: ${certificate.date_generated}",
                480f, 14f
            )

            drawCenteredText(
                canvas, paint,
                "Identificador: ${certificate.id}",
                535f, 11f
            )

            document.finishPage(page)
            document.writeTo(outputStream)

        } finally {
            document.close()
        }
    }

    private fun drawCenteredText(
        canvas: Canvas,
        paint: Paint,
        text: String,
        y: Float,
        size: Float,
        bold: Boolean = false
    ) {
        paint.color = Color.BLACK
        paint.textSize = size
        paint.textAlign = Paint.Align.CENTER

        paint.typeface = if (bold) {
            Typeface.create("serif", Typeface.BOLD)
        } else {
            Typeface.create("serif", Typeface.NORMAL)
        }

        canvas.drawText(text, 421f, y, paint)
    }

    private fun drawParagraph(
        canvas: Canvas,
        text: String,
        y: Float,
        size: Float,
        bold: Boolean = false
    ) {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = size

            typeface = Typeface.create(
                "serif",
                if (bold) Typeface.BOLD else Typeface.NORMAL
            )
        }

        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, 700)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(5f, 1f)
            .build()

        canvas.save()
        canvas.translate(71f, y)
        layout.draw(canvas)
        canvas.restore()
    }
}