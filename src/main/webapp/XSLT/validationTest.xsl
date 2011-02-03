<?xml version="1.0" encoding="UTF-8"?>
	<!--
		* Copyright 2010 OpenHealthData, Inc. * * This program is free
		software: you can redistribute it and/or modify * it under the terms
		of the GNU Affero General Public License as * published by the Free
		Software Foundation, either version 3 of the * License, or (at your
		option) any later version. * * This program is distributed in the hope
		that it will be useful, * but WITHOUT ANY WARRANTY; without even the
		implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR
		PURPOSE. See the * GNU Affero General Public License for more details.

		* You should have received a copy of the GNU Affero General Public
		License * along with this program. If not, see
		<http://www.gnu.org/licenses/>.
	-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:val="http://openhealthdata.org/XML/ValidationResult">
	<xsl:template match="/">
		<html>
			<head>
				<title>CCR Validation Test</title>
			</head>
			<body bgcolor="#ADD8E6">
				<xsl:apply-templates select="val:ValidationResult" />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="val:ValidationResult">
		<h2>CCR Validation Results</h2>
		<table width="50%" border="0">
			<xsl:if test="/val:ValidationResult/val:TestFile">
				<tr>
					<td>
						<b>File:</b>
					</td>
					<td>
						<xsl:value-of select="/val:ValidationResult/val:TestFile" />
					</td>
				</tr>
			</xsl:if>
			<tr>
				<td>
					<b>Date:</b>
				</td>
				<td>
					<xsl:value-of select="/val:ValidationResult/val:Timestamp" />
				</td>
			</tr>
			<tr>
				<td>
					<b>Schema:</b>
				</td>
				<td>
					<xsl:value-of select="/val:ValidationResult/val:ValidationUsed/val:Schema" />
				</td>
			</tr>
			<tr>
				<td>
					<b>Profile(s):</b>
				</td>
				<td>
					<xsl:for-each
						select="/val:ValidationResult/val:ValidationUsed/val:Profile">
						<xsl:value-of select="val:Name" />
						<xml:text> (</xml:text>
						<xsl:value-of select="@id" />
						<xml:text>)</xml:text>
						<xsl:if test="position() != last()">
							<xsl:text>,  </xsl:text>
						</xsl:if>
					</xsl:for-each>
				</td>
			</tr>
		</table>
		<br />
		<xsl:if test="/val:ValidationResult/val:Disclaimer">
			<table width="50%" border="0">
				<tr>
					<td bgcolor="#FFFF66">
						<p>
							<font color="#FF0000">
								<strong>Disclaimer:</strong>
							</font>
						</p>
						<p>
							<xsl:value-of select="/val:ValidationResult/val:Disclaimer" />
						</p>
					</td>
				</tr>

			</table>
			<br />
		</xsl:if>
		<xsl:choose>
			<xsl:when test="/val:ValidationResult/@status='failed'">
				<font size="4">
					<b>Validation Result:</b>
				</font>
				<font size="4" color="red"> FAILED</font>
			</xsl:when>
			<xsl:when test="/val:ValidationResult/@status='passed'">
				<font size="4">
					<b>Validation Result:</b>
				</font>
				<font size="4" color="green"> PASSED</font>
			</xsl:when>
			<xsl:otherwise>
				<font size="4">
					<b>Validation Result:</b>
				</font>
				<font size="4" color="yellow"> UNKNOWN</font>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:for-each select="val:TestResult">
			<p>
				<b>Test: </b>
				<xsl:value-of select="val:TestDescription/val:Name" />
				<xml:text> [Profile: </xml:text>
				<xsl:value-of select="val:TestDescription/@profile" />
				<xml:text>]</xml:text>
				<hr />
				<b>Result: </b>
				<xsl:choose>
					<xsl:when test="@status='passed'">
						<font color="green">
							Passed
                        </font>
					</xsl:when>
					<xsl:when test="@status='failed'">
						<font color="red">
							Failed
                        </font>
					</xsl:when>
					<xsl:when test="@status='missing'">
						<font color="green">
							Missing Optional Section - No test performed
                        </font>
					</xsl:when>
				</xsl:choose>
			</p>
			<p>
				<b>Description</b>
				<br />
				<xsl:value-of select="val:TestDescription/val:Description" />
			</p>
			<xsl:if test="val:Error">
				<p>
					<b>Errors</b>
					<table width="75%" border="1">
						<tbody>
							<tr>
								<th>Severity</th>
								<th>Location</th>
								<th>Message</th>
							</tr>
							<xsl:apply-templates select="val:Error" />
						</tbody>
					</table>
				</p>
			</xsl:if>
		</xsl:for-each>
		<xsl:apply-templates select="./val:Rules" />
	</xsl:template>

	<xsl:template match="val:Error">
		<tr>
			<td>
				<xsl:choose>
					<xsl:when test="@serverity='warn'">
						<font color="yellow">
							Warning
                        </font>
					</xsl:when>
					<xsl:when test="@serverity='fatal'">
						<font color="red">
							Fatal
                        </font>
					</xsl:when>
					<xsl:when test="@serverity='info'">
						<font color="green">
							FYI
                        </font>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:if test="val:InFileLocation">
					<xsl:text>Line:</xsl:text>
					<xsl:value-of select="val:InFileLocation/val:LineNumber" />
					<xsl:text> Column:</xsl:text>
					<xsl:value-of select="val:InFileLocation/val:ColumnNumber" />
				</xsl:if>
				<xsl:if test="val:InFileLocation and val:XPathLocation">
					<br />
				</xsl:if>
				<xsl:if test="val:XPathLocation">
					<xsl:value-of select="valXPathLocation" />
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="val:Message" />
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="val:Rules">
		<hr style="border: 2px solid #000;" />
		<h2>Rules Evaluated In Addition to XML Schema Check</h2>
		<table width='75%' border='1' bgcolor='#C0C0C0'>
			<tr>
				<th>Package</th>
				<th>Name</th>
				<th>Description</th>
			</tr>
			<xsl:apply-templates select="val:Rule" />
		</table>
	</xsl:template>

	<xsl:template match="val:Rule">
		<xsl:if test="./val:Package != 'org.astm.ccr.rules.core'">
			<tr>
				<td>
					<xsl:value-of select="./val:Package" />
				</td>
				<td>
					<xsl:value-of select="./val:Name" />
				</td>
				<td>
					<xsl:value-of select="./val:Description" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
