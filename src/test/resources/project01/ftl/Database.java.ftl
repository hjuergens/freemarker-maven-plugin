package ftl;

import java.util.List;
import java.util.LinkedList;

 /**
 * description here.
 * Today is ${.now?date}
 */
public class Database {
    <#list workbook as sheet>

    <#assign TableName = "Table${sheet.name?cap_first}">

    public class ${TableName} {

        <#assign CELL_TYPE_NUMERIC = 0>
        <#assign CELL_TYPE_STRING = 1>
        <#assign CELL_TYPE_FORMULA = 2>
        <#assign CELL_TYPE_BLANK = 3>
        <#assign CELL_TYPE_BOOLEAN = 4>
        <#assign CELL_TYPE_ERROR = 5>

        public class Row {
        <#list sheet.columns as column>
            protected
            <#if column.type == "1">
                String
            <#elseif column.type == "0">
                Number
            <#elseif column.type == "4">
                boolean
            <#else>
                OOPS
            </#if>
            ${column.name?uncap_first};
        <#else>
            // no columns in table ${TableName}
        </#list>
                }

                protected final List<Row> rows;

            public ${TableName}() {
            rows = new LinkedList<Row>();
                <#list sheet as row>
                <#if row??>
                {
                    Row tmp = new Row();

                <#list sheet.columns as column>
                    <#if column?index < row.size>
                    tmp.${column.name?uncap_first} =
                    <#if column.type == "2">
                        "${row[column?index].value?j_string}";
                    <#elseif column.type == "0">
                        ${row[column?index].value};
                    <#elseif column.type == "4">
                        ${row[column?index].value?c};
                    <#else>
                        null;
                    </#if>
                    </#if>
                <#else>
                    // no columns in table ${TableName}
                </#list>
                    rows.add(tmp);
                }
            <#else>
            // missed something here
        </#if>
        </#list>
        }
    }

    protected ${TableName} table${sheet.name?cap_first};

    /**
    * Ruft den Wert der table${sheet.name?cap_first}-Eigenschaft ab.
    *
    * @return possible object is {@link ${TableName} }
    *
    */
    public ${TableName} getTable${sheet.name?cap_first}() {
        return table${sheet.name};
    }

    </#list>
}
