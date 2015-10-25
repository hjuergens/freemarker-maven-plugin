<?xml version="1.0" encoding="UTF-8"?>
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<model xsd.namespace="http://modello.codehaus.org/MODELLO/1.0.0"
       xml.schemaLocation="http://modello.codehaus.org/xsd/modello-1.0.0.xsd"
       xml.namespace="de.juergens.data"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://modello.codehaus.org/MODELLO/1.0.0 http://modello.codehaus.org/xsd/modello-1.0.0.xsd"
       xmlns="http://modello.codehaus.org/MODELLO/1.0.0">
    <id>exceldata</id>
    <name>ExcelData</name>
    <description><![CDATA[EXCELDATA]]></description>
    <defaults>
        <default>
            <key>package</key>
            <value>net.sf.hajuergens.data</value>
        </default>
    </defaults>
    <classes>
        <class>
            <name>Cell</name>
            <description><![CDATA[Put the description here.]]></description>
            <version>1.0.0</version>
            <fields>
                    <field>
                        <name>content</name>
                        <required>true</required>
                        <description><![CDATA[content]]></description>
                        <type>String</type>
                    </field>
                    <field>
                        <name>type</name>
                        <required>true</required>
                        <description><![CDATA[type]]></description>
                        <defaultValue>CELL_TYPE_STRING</defaultValue>
                        <type>String</type>
                    </field>
            </fields>
        </class>

        <#list workbook as sheet>
        <class>
            <name>ROW_${sheet.name}</name>
            <description><![CDATA[Put the description here.]]></description>
            <version>1.0.0</version>
            <fields>
                    <#list sheet.columns as column>
                    <field>
                        <name>${column.name}</name>
                        <required>true</required>
                        <description><![CDATA[${column.name}]]></description>
                        <type>${column.type}</type>
                    </field>
                    </#list>
            </fields>
        </class>
        </#list>

        <#list workbook as sheet>
        <class>
            <name>TABLE_${sheet.name}</name>
            <description><![CDATA[description here.]]></description>
            <version>1.0.0</version>
            <fields>
                <field>
                    <name>rows</name>
                    <version>1.0.0</version>
                    <association xml.itemsStyle="wrapped">
                        <type>ROW_${sheet.name}</type>
                        <multiplicity>*</multiplicity>
                    </association>
                </field>
            </fields>
        </class>
        </#list>

    </classes>
</model>
