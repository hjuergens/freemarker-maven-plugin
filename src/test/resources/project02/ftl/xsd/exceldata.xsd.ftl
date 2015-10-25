<?xml version="1.0"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" xmlns="de.juergens.data" targetNamespace="de.juergens.data">
  <xs:element name="database" type="Database">
    <xs:annotation>
      <xs:documentation source="version">1.0.0</xs:documentation>
      <xs:documentation source="description">description here.</xs:documentation>
    </xs:annotation>
  </xs:element>
  <xs:complexType name="Database">
    <xs:annotation>
      <xs:documentation source="version">1.0.0</xs:documentation>
      <xs:documentation source="description">description here.</xs:documentation>
    </xs:annotation>
    <xs:sequence>
      <xs:element minOccurs="0" maxOccurs="unbounded" name="table" type="TABLE">
        <xs:annotation>
          <xs:documentation source="version">1.0.0</xs:documentation>
          <xs:documentation source="description">description here.</xs:documentation>
        </xs:annotation>
      </xs:element>
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="TABLE">
    <xs:annotation>
      <xs:documentation source="version">1.0.0</xs:documentation>
      <xs:documentation source="description">description here.</xs:documentation>
    </xs:annotation>
    <xs:sequence>
      <xs:element minOccurs="0" name="rows">
        <xs:annotation>
          <xs:documentation source="version">1.0.0</xs:documentation>
        </xs:annotation>
        <xs:complexType>
          <xs:sequence>
            <xs:element name="row" minOccurs="0" maxOccurs="unbounded" type="ROW"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
    </xs:sequence>
  </xs:complexType>

  <#list workbook as sheet>
  <xs:complexType name="ROW_${sheet.name}">
    <xs:annotation>
      <xs:documentation source="version">1.0.0</xs:documentation>
      <xs:documentation source="description">Put the description here.</xs:documentation>
    </xs:annotation>
    <xs:sequence>
      <#list sheet.columns as column>
      <xs:element minOccurs="0" name="${column.name}" type="xs:string">
        <xs:annotation>
          <xs:documentation source="version">0.0.0+</xs:documentation>
          <xs:documentation source="description">${column.name}</xs:documentation>
        </xs:annotation>
      </xs:element>
      </#list>
    </xs:sequence>
  </xs:complexType>
  </#list>
</xs:schema>