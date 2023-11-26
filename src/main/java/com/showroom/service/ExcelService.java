package com.showroom.service;

import java.time.LocalDate;
import java.util.List;

public interface ExcelService {

    public byte[] generateExcel(String sheetName,List<Object[]> result,List<String> rowHeader,  LocalDate start, LocalDate end);
}
