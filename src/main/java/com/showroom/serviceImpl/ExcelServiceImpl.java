package com.showroom.serviceImpl;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.showroom.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public byte[] generateExcel(String sheetName, List<Object[]> result, List<String> rowHeader) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(sheetName);
            HSSFRow row = sheet.createRow(0);

            int rowHeaderCellIndex = 0;
            // create header cell for given header list
            while (rowHeaderCellIndex < rowHeader.size()) {
                row.createCell(rowHeaderCellIndex).setCellValue(rowHeader.get(rowHeaderCellIndex));
                rowHeaderCellIndex++;
            }

            int rowDataIndex = 1;
            for (Object[] o : result) {
                HSSFRow dataRow = sheet.createRow(rowDataIndex);
                dataRow.createCell(0).setCellValue((String) o[0]);
                dataRow.createCell(1).setCellValue(String.valueOf(o[1]));
                BigDecimal totalRevenue = new BigDecimal((Double) o[2]).setScale(2, RoundingMode.HALF_UP);
                dataRow.createCell(2).setCellValue(String.valueOf(totalRevenue));
                rowDataIndex++;
            }
            File file = new File("D:\\excel.xls");
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file, false);
            workbook.write(outputStream);

            workbook.write(byteArrayOutputStream);
        } catch (IOException e) {
            log.error("Error in generateExcel {}", e);
            throw new RuntimeException("Excel not generate");
        }
        return byteArrayOutputStream.toByteArray();
    }


}
