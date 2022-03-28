# Intro

If you are familiar with maven you already know the interpolation task.


This Maven plugin Integrates FreeMarker in a maven build.
So you can specify templates with keys which will be replaced during the maven build process.

The plugin needs three parameter:
* templateFile
* modelFile
* resultDirectory

### templateFile
A Template for the generation of any text/source file.

### modelFile
Specifies the input data for the interpolation process.

### resultDirectory
Specified the destination directory.


## pom

To use the plugin add an additional tag:
```

  <build>
    <plugins>
      <plugin>
        <artifactId>freemarker-maven-plugin</artifactId>
          <configuration>
            <templateFile>...</templateFile>
            <modelFile>...</modelFile>
            <resultDirectory>...</resultDirectory>
          </configuration>
      </plugin>
    </plugins>
  </build>
```

Thre procedure is best explained by an example

# Model

An Excel sheet contains the following table (extract): 

| Name	| CCY	| checked	| Date |
| --- | --- | --- | --- |
|RBA Interbank Overnight CashRate|	AUD|	WAHR	|03.02.20|
| ...|...| ...| ... |
|WIBOR	|PLN	|0	|03.05.20|

# Template

A part of a template which generates a java source file:
```
                {
                    Row tmp = new Row();

                <#list sheet.columns as column>
                    <#if column?index < row.size>
                    tmp.${column.name?uncap_first} =
                    <#if column.type == CELL_TYPE_STRING>
                        "${row[column?index].value?j_string}";
                    <#elseif column.type == CELL_TYPE_NUMERIC>
                        ${row[column?index].value};
                    <#elseif column.type == CELL_TYPE_BOOLEAN>
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
```

# Output

The result of the build process is than a java source file which contains 

```
{
    Row tmp = new Row();

    tmp.name = "RBA Interbank Overnight CashRate";
    tmp.cCY = "AUD";
    tmp.checked = 1;
    tmp.date = 42.302;
    rows.add(tmp);
}
...
{
    Row tmp = new Row();
    
    tmp.name = "WIBOR";
    tmp.cCY = "PLN";
    tmp.checked = 0;
    tmp.date = 42.392;
    rows.add(tmp);
}

```

You may find another example in the test directory.
