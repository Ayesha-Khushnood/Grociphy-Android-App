package com.example.homepage.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.homepage.Domain.OrderModel;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class pdfUtility {

    public static void generateOrderReport(Context context, List<OrderModel> orders) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        paint.setTextSize(24);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Order Report", 50, 50, paint);

        paint.setTextSize(12);
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        canvas.drawText("Generated on: " + date, 50, 80, paint);

        // Safe logo loading (uncomment and replace drawable if needed)
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        if (logo != null && !logo.isRecycled()) {
            canvas.drawBitmap(logo, 400, 30, paint);
        } else {
            Log.e("PDF", "Logo bitmap is null or recycled.");
        }

        paint.setStrokeWidth(2f);
        canvas.drawLine(50, 100, 545, 100, paint);

        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Order ID", 50, 130, paint);
        canvas.drawText("Customer", 150, 130, paint);
        canvas.drawText("Items", 300, 130, paint);
        canvas.drawText("Total", 450, 130, paint);
        canvas.drawText("Status", 520, 130, paint);

        canvas.drawLine(50, 140, 545, 140, paint);

        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        int yPos = 170;

        for (OrderModel order : orders) {
            canvas.drawText(order.getOrderId().substring(0, 8), 50, yPos, paint);
            canvas.drawText(order.getUserName(), 150, yPos, paint);

            // Build item string
            StringBuilder items = new StringBuilder();
            for (PopularDomain item : order.getItems()) {
                items.append(item.getTitle()).append("(").append(item.getNumberinCart()).append("), ");
            }

            // Draw wrapped items
            int maxWidth = 140;
            int xItem = 300;
            int lineHeight = 14;
            String itemsText = items.toString();
            String[] words = itemsText.split(" ");
            StringBuilder line = new StringBuilder();
            int itemLineY = yPos;

            for (String word : words) {
                if (paint.measureText(line + word + " ") > maxWidth) {
                    canvas.drawText(line.toString(), xItem, itemLineY, paint);
                    line = new StringBuilder();
                    itemLineY += lineHeight;
                }
                line.append(word).append(" ");
            }
            if (line.length() > 0) {
                canvas.drawText(line.toString(), xItem, itemLineY, paint);
            }

            canvas.drawText(String.format("$%.2f", order.getTotalPrice()), 450, yPos, paint);

            paint.setColor(order.getStatus().equals("completed") ? Color.GREEN : Color.RED);
            canvas.drawText(order.getStatus(), 520, yPos, paint);
            paint.setColor(Color.BLACK);

            yPos = Math.max(yPos + 30, itemLineY + 20);

            if (yPos > 800) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                yPos = 50;
            }
        }

        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Summary", 50, yPos + 40, paint);

        long completed = orders.stream().filter(o -> o.getStatus().equals("completed")).count();
        long pending = orders.size() - completed;
        double totalIncome = orders.stream()
                .filter(o -> o.getStatus().equals("completed"))
                .mapToDouble(OrderModel::getTotalPrice)
                .sum();

        canvas.drawText("Total Orders: " + orders.size(), 50, yPos + 70, paint);
        canvas.drawText("Completed: " + completed, 50, yPos + 100, paint);
        canvas.drawText("Pending: " + pending, 50, yPos + 130, paint);
        canvas.drawText("Total Income: $" + String.format("%.2f", totalIncome), 50, yPos + 160, paint);

        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "OrderReport_" + System.currentTimeMillis() + ".pdf";
        File file = new File(downloadsDir, fileName);

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
}
