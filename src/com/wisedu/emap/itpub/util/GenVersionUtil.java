/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.util
 * @date 2016年6月2日 上午11:04:14
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.util;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;

/**
 * @filename GenVersionUtil.java
 * @date 2016年6月2日 上午11:04:14
 * @author wjfu 01116035
 */
public class GenVersionUtil {

    private GenVersionUtil() {
    }

    public static List<Map<String, Object>> getItTableNames() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT                        ");
        sql.append("     A.TABLE_NAME VALUE        ");
        sql.append("    ,B.COMMENTS LABEL        ");
        sql.append(" FROM                        ");
        sql.append("        USER_TABLES A        ");
        sql.append("    LEFT JOIN                ");
        sql.append("        USER_TAB_COMMENTS B    ");
        sql.append("    ON                        ");
        sql.append("        A.TABLE_NAME = B.TABLE_NAME    ");
        sql.append(" WHERE A.TABLE_NAME LIKE ? OR A.TABLE_NAME LIKE ? ");
        return DbUtil.query(sql.toString(), new Object[] { "T_%", "V_%" });
    }

    /**
     * 根据sql语句生成插入语句
     * 
     * @author xianghao
     * @date 2016年8月17日 下午2:57:20
     * @param insertSql
     * @param out
     * @return
     * @throws Exception
     */
    public static String genInsertVersionXml(Map<String, Object> paramMap,
            OutputStream out) throws Exception {
        String jsonParam = StringUtil.getString(paramMap.get("param"));
        Map<String, Object> param = JsonUtils.json2Map(jsonParam);
        String excuteSql = StringUtil.getString(param.get("excuteSql"));
        String tableName = StringUtil.getString(param.get("tableName"));
        String uniqueField = StringUtil.getString(param.get("uniqueField"));
        String autoGenKey = StringUtil.getString(param.get("autoGenKey"));
        List<Map<String, Object>> resultList = DbUtil.query(excuteSql);
        Element root = DocumentHelper.createElement("dbVersion");
        Document document = DocumentHelper.createDocument(root);
        for (Map<String, Object> map : resultList) {
            Element element = root.addElement("data");
            StringBuilder sb = new StringBuilder();
            String filed = StringUtil.getString(map.get(uniqueField));
            sb.append("\r\n\t\tMERGE INTO {0} T USING (SELECT ").append("''").append("{1}").append("'' {2} FROM dual) N ON (T.{2} = N.{2})");
            sb.append("\r\n\t\tWHEN NOT MATCHED THEN ");
            sb.append("\r\n\t\tINSERT(");
            // 对每个map进行循环
            StringBuilder valueSB = new StringBuilder();
            valueSB.append("\r\n\t\tVALUES (");
            Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
            while(it.hasNext()) {
                Entry<String, Object> entry =  it.next();
                String key = entry.getKey();
                String value = StringUtil.getString(entry.getValue());
                // 获取key value
                if(it.hasNext()) {
                    sb.append(key).append(", ");
                    // 如果可以自动生成key 则认为使用SYS_GUID否则 使用数据库key
                    if(Constants.YES.equals(autoGenKey) && "WID".equals(key)) {
                        valueSB.append(" SYS_GUID(),");
                    } else {
                        valueSB.append("''").append(value).append("''").append(", ");
                    }
                }else {
                    sb.append(key + ")");
                    if(Constants.YES.equals(autoGenKey) && "WID".equals(key)) {
                        valueSB.append(" SYS_GUID())");
                    } else {
                        valueSB.append("''").append(value).append("''").append(")");
                    }
                }
            }
            sb.append(valueSB.toString()).append("\n\t");
            String text = MessageFormat.format(sb.toString(), tableName, filed, uniqueField);
            element.addText(text);
            root.addText("\r\n");
        }
        // 把生成的xml文档存放在硬盘上 true代表是否换行
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("UTF-8");// 设置编码格式
        XMLWriter xmlWriter = new XMLWriter(out, format);
        xmlWriter.write(document);
        xmlWriter.close();
        return document.asXML();
    }
    
    private static List<Map<String, Object>> tablesInfo(List<String> tableNames) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT                      ");
        sql.append("     A.TABLE_NAME           ");
        sql.append("    ,C.COMMENTS T_COMMENTS  ");
        sql.append("    ,A.COLUMN_NAME          ");
        sql.append("    ,A.DATA_TYPE            ");
        sql.append("    ,A.DATA_LENGTH          ");
        sql.append("    ,A.NULLABLE             ");
        sql.append("    ,A.DATA_DEFAULT         ");
        sql.append("    ,B.COMMENTS             ");
        sql.append("FROM                        ");
        sql.append("        USER_TAB_COLUMNS A  ");
        sql.append("    LEFT JOIN               ");
        sql.append("        USER_COL_COMMENTS B ");
        sql.append("    ON                      ");
        sql.append("        A.COLUMN_NAME = B.COLUMN_NAME AND A.TABLE_NAME = B.TABLE_NAME");
        sql.append("    LEFT JOIN               ");
        sql.append("        USER_TAB_COMMENTS C ");
        sql.append("    ON                      ");
        sql.append("        A.TABLE_NAME = C.TABLE_NAME ");
        sql.append("WHERE                       ");
        sql.append("    A.TABLE_NAME IN         ");
        sql.append(DbUtil.genWhereInSub(tableNames.size()));
        return DbUtil.defaultEmptyQuery(sql.toString(), tableNames.toArray());
        
    }
    
    /**
     * @date 2016年6月2日 上午11:04:14
     * @author wjfu 01116035
     * @param args
     */
    public static String genDbVersionXml(List<String> tableNames,
            OutputStream out) throws Exception {
        List<Map<String, Object>> results = tablesInfo(tableNames);
        Map<String, TableMeta> tableMetaMap = Maps.newLinkedHashMap();
        for (Map<String, Object> result : results) {
            String tabName = StringUtil.getString(result.get("TABLE_NAME"));
            TableMeta tm = tableMetaMap.get(tabName);
            if (null == tm) {
                tm = new TableMeta();
                tm.setTabName(tabName);
                tm.setTabComment(StringUtil.getString(result.get("T_COMMENTS")));
                tableMetaMap.put(tabName, tm);
            }
            List<ColMeta> colMetas = tm.getColMeta();
            ColMeta colMeta = new ColMeta();
            colMeta.setColName(StringUtil.getString(result.get("COLUMN_NAME")));
            colMeta.setColType(getType(result.get("DATA_TYPE"),
                    result.get("DATA_LENGTH")));
            colMeta.setComment(StringUtil.getString(result.get("COMMENTS")));
            colMeta.setDataDefault(StringUtil.getString(result
                    .get("DATA_DEFAULT")));
            colMeta.setNullable("Y".equals(result.get("NULLABLE")));
            colMetas.add(colMeta);
        }
        Element root = DocumentHelper.createElement("dbVersion");
        Document document = DocumentHelper.createDocument(root);
        Iterator<Map.Entry<String, TableMeta>> iter = tableMetaMap.entrySet()
                .iterator();
        while (iter.hasNext()) {
            Map.Entry<String, TableMeta> me = iter.next();
            TableMeta table = me.getValue();
            // 给根节点添加孩子节点
            root.addComment(table.getTabName());
            Element tb = root.addElement("table");
            Element idx = root.addElement("index");
            tb.addAttribute("name", table.getTabName());
            tb.addAttribute("desc", table.getTabComment());
            //Collections.reverse(table.getColMeta());
            for (ColMeta col : table.getColMeta()) {
                Element colE = tb.addElement("column");
                colE.addAttribute("name", col.getColName());
                colE.addAttribute("type", col.getColType());
                colE.addAttribute("desc", col.getComment());
                colE.addAttribute("nullable", String.valueOf(col.getNullable()));
                if (StringUtil.isNotEmpty(col.getDataDefault())) {
                    colE.addAttribute("default", col.getDataDefault());
                }
            }
            idx.addAttribute("name", "PK_" + table.getTabName())
                    .addAttribute("type", "key")
                    .addAttribute("tableName", table.getTabName());
            idx.addElement("column").addAttribute("name", "WID");
        }
        // 把生成的xml文档存放在硬盘上 true代表是否换行
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("UTF-8");// 设置编码格式
        XMLWriter xmlWriter = new XMLWriter(out, format);
        xmlWriter.write(document);
        xmlWriter.close();
        return document.asXML();

    }

    private final static Map<String, String> TYPE_MAP = ImmutableMap
            .<String, String> builder().put("VARCHAR2", "String")
            .put("NUMBER", "double").put("DATE", "Datetime")
            .put("CLOB", "Clob").put("BLOB", "Blob").build();

    private static String getType(Object type, Object len) {
        String typeStr = TYPE_MAP.get(StringUtil.getString(type));
        if (StringUtil.isNotEmptyObj(len) && "String".equals(typeStr)) {
            typeStr += "(" + len + ")";
        }
        return typeStr;
    }

    public static void genDbExcelInfo(List<String> tableNames, OutputStream out) throws Exception {
        List<Map<String, Object>> results = tablesInfo(tableNames);
        //Collections.reverse(results);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        Map<String, List<Map<String, Object>>> tableMetaMap = Maps.newLinkedHashMap();
        for (Map<String, Object> result : results) {
            String tabName = StringUtil.getString(result.get("TABLE_NAME"));
            List<Map<String, Object>> tabCols = tableMetaMap.get(tabName);
            if(tabCols == null) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("COLUMN_NAME", "字段名");
                map.put("COMMENTS", "解释");
                map.put("DATA_TYPE", "类型");
                map.put("DATA_LENGTH", "大小");
                tabCols = Lists.newArrayList();
                tabCols.add(map);
                tableMetaMap.put(tabName, tabCols);
            }
            tabCols.add(result);
        }
        int rowIdx = 0;
        for(Map.Entry<String, List<Map<String, Object>>> me : tableMetaMap.entrySet()) {
            String tableName = me.getKey();
            // 创建表头
            HSSFRow rowHead = sheet.createRow(rowIdx++);
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIdx + ":$D$" + rowIdx));
            sheet.setColumnWidth(0, 3000); //设置宽度 （excel的宽度单位不同）
            sheet.setColumnWidth(1, 3500);
            sheet.setColumnWidth(2, 3500);
            sheet.setColumnWidth(3, 2000);
            Cell cellHead = rowHead.createCell(0);
            cellHead.setCellValue(tableName);
            List<Map<String, Object>> cols = me.getValue();
            for(Map<String, Object> col: cols) {
                HSSFCellStyle cellStyle = wb.createCellStyle();
                // 设置这些样式
                cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置单元格为水平对齐的类型
                cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //设置垂直居中
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                
                HSSFCellStyle cellStyle1 = wb.createCellStyle();
                cellStyle1.cloneStyleFrom(cellStyle);
                cellStyle1.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                HSSFFont font = wb.createFont();
                font.setColor(HSSFColor.WHITE.index);
                cellStyle1.setFont(font);
                
                // 创建表内的行
                HSSFRow row = sheet.createRow(rowIdx++);
                // 创建每一行的数据
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(StringUtil.getString(col.get("COLUMN_NAME")));
                
                HSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(StringUtil.getString(col.get("COMMENTS")));
                
                HSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(StringUtil.getString(col.get("DATA_TYPE")));
                
                HSSFCell cell3 = row.createCell(3);
                cell3.setCellType(Cell.CELL_TYPE_NUMERIC);
                // cell3.setCellValue((StringUtil.getString(col.get("DATA_LENGTH"))));
                
                if("字段名".equals(StringUtil.getString(col.get("COLUMN_NAME")))) {
                    cell0.setCellStyle(cellStyle1);
                    cell1.setCellStyle(cellStyle1);
                    cell2.setCellStyle(cellStyle1);
                    cell3.setCellStyle(cellStyle1);
                    cell3.setCellValue(StringUtil.getString(col.get("DATA_LENGTH")));
                } else {
                    cell0.setCellStyle(cellStyle);
                    cell1.setCellStyle(cellStyle);
                    cell2.setCellStyle(cellStyle);
                    cell3.setCellStyle(cellStyle);
//                    cell3.setCellType(Cell.CELL_TYPE_NUMERIC);
//                    cell3.setCellValue(NumberUtils.createInteger((StringUtil.getString(col.get("DATA_LENGTH")))));
                }
            }
        }
        wb.write(out);
    }
    
}

class TableMeta {

    private String tabName;

    private String tabComment;

    private List<ColMeta> colMeta = Lists.newArrayList();

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabComment() {
        return tabComment;
    }

    public void setTabComment(String tabComment) {
        this.tabComment = tabComment;
    }

    public List<ColMeta> getColMeta() {
        return colMeta;
    }

    public void setColMeta(List<ColMeta> colMeta) {
        this.colMeta = colMeta;
    }
}

class ColMeta {

    private String colName;

    private String colType;

    private String comment;

    private boolean nullable;

    private String dataDefault;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDataDefault() {
        return dataDefault;
    }

    public void setDataDefault(String dataDefault) {
        this.dataDefault = dataDefault;
    }

}
