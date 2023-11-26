package com.showroom.serviceImpl;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.showroom.entity.Customer;
import com.showroom.entity.Order;
import com.showroom.entity.ShowRoomDetails;
import com.showroom.service.PdfGeneratorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {
    @Override
    public byte[] generatePdf(Customer customer, Order order, ShowRoomDetails showRoomDetails) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        // heading
        document.add(((Paragraph) createParagraph("MyDrive")).setFontColor(Color.BLUE).setFontSize(40).setMarginBottom(10).setItalic());

        //page title
        document.add(((Paragraph) createParagraph("Billing Details")).setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(20));

        // Billing details
        document.add(((Paragraph) createParagraph("Bill id : " + order.getId())).setMargin(0).setPadding(0));
        document.add(((Paragraph) createParagraph("Bill date : " + LocalDate.now())).setMargin(0).setPadding(0));

        // Supplier details
        document.add(((Paragraph) createParagraph("Showroom details :")).setPadding(0).setMargin(0).setMarginTop(20).setBold());
        Table showroomTable = new Table(new float[]{400, 400});
        LinkedHashMap<String, String> showroomTableColNames = new LinkedHashMap<>();
        showroomTableColNames.put("name ",showRoomDetails.getName());
        showroomTableColNames.put("Address ",showRoomDetails.getAddress());
        document.add(createTable(showroomTableColNames));

        // Customer details
        document.add(((Paragraph) createParagraph("Customer details :")).setPadding(0).setMargin(0).setMarginTop(20).setBold());
        LinkedHashMap<String, String> customerTableColNames = new LinkedHashMap<>();
        customerTableColNames.put("Customer name",customer.getFirstName());
        customerTableColNames.put("Phone no", customer.getPhoneNo());
        customerTableColNames.put("Email",customer.getEmail());
        customerTableColNames.put("Address",customer.getAddress());
        document.add(createTable(customerTableColNames));

        // order details
        Table table = new Table(new float[]{400, 100, 100, 100, 100, 300, 100, 500});
        Map<String, Map<String, Object>> colNames = new LinkedHashMap<>();
        colNames.put("Vehicle Name", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("Quantity", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("vehicle", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("Color", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("Fuel Type", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("Initial price", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("Discount", Map.of("textAlign", TextAlignment.CENTER));
        colNames.put("additional Charges", Map.of("textAlign", TextAlignment.CENTER));
        colNames.forEach((k, v) -> {
            table.addHeaderCell(createCell(k, v));
        });

        order.getOrderItem().forEach(orderItem -> {
            Map<String, Map<String, Object>> bodyColumn = new LinkedHashMap<>();
            bodyColumn.put(orderItem.getVehicle().getName(), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(String.valueOf(orderItem.getQuantity()), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(orderItem.getVehicle().getVehicleType().name(), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(orderItem.getColor().name(), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(orderItem.getFuelType().name(), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(String.valueOf(orderItem.getInitialPrice()), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(String.valueOf(orderItem.getDiscount()), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.put(String.valueOf(orderItem.getAdditionalCharges()), Map.of("textAlign", TextAlignment.CENTER));
            bodyColumn.forEach((k, v) -> {
                table.addCell(createCell(k, v));
            });

        });
        table.setMarginTop(30);
        table.setMarginBottom(20);
        document.add(table);
        // order total
        document.add(((Paragraph) createParagraph("Order total : Rs " + order.getTotal())).setTextAlignment(TextAlignment.RIGHT).setFontSize(20));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }


    private IBlockElement createParagraph(String text){
        return new Paragraph(text);
    }

    private Cell createCell(String text, Map<String, Object> properties){
        Cell cell = new Cell();
        cell.add(text);
        properties.forEach((k, v) -> {
            switch (k) {
                case "textAlignment" -> cell.setTextAlignment((TextAlignment) v);
            }
        });
        return cell;
//        return new Cell().add(text).setTextAlignment(TextAlignment.CENTER);
    }

    private Table createTable(LinkedHashMap<String,String> columns){
        Table table = new Table(new float[]{400, 400});
        columns.forEach((k,v)->{
            table.addCell(createCell(k,Map.of("textAlign", TextAlignment.CENTER)));
            table.addCell(createCell(v,Map.of("textAlign", TextAlignment.CENTER)));
        });
        return  table;
    }
}
