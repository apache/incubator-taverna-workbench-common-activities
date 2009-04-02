<?xml version="1.0" encoding="utf-8"?>

<!-- ===================================================================== -->
<!-- Converting analysis metadata returned by Soaplab Web Services to HTML -->
<!-- (http://www.ebi.ac.uk/soaplab/)                                       -->
<!-- Author: Martin Senger (senger@ebi.ac.uk)                              -->
<!-- ===================================================================== -->

<!-- $Id: analysis_metadata_2_html.xsl,v 1.1 2008/07/14 15:27:42 iandunlop Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:output method="html"/>

  <!-- the main document body -->
  <xsl:template match="/">
      <body>
        <xsl:apply-templates/>
      </body>
  </xsl:template>

  <!-- analysis -->
  <xsl:template match="/DsLSRAnalysis/analysis">

    <!-- analysis name -->
    <table border="0" cellpadding="2" cellspacing="3" width="98%" align="center"><tr><th bgcolor="#eeeedd">
    <font size="+1"><xsl:value-of select="@name"/></font>
    </th></tr></table>

    <!-- analysis metadata -->
    <table border="0" cellspacing="1" cellpadding="1" width="99%" align="center"><tr>
    <td>
      <table border="0" cellspacing="2">
        <xsl:apply-templates select="description" mode="as-row"/>
        <xsl:apply-templates select="analysis_extension/app_info/@*[local-name != 'help_URL']" mode="as-row"/>
        <xsl:apply-templates select="@*[local-name() != 'name']" mode="as-row"/>
	<tr><td>Help URL</td><td><a href="{analysis_extension/app_info/@help_URL}"><xsl:value-of select="analysis_extension/app_info/@help_URL"/></a></td></tr>
      </table>
    </td></tr></table>

    <!-- inputs/outputs metadata -->
    <table border="0" width="98%" cellpadding="2" cellspacing="1" align="center">
      <tr><td colspan="2" bgcolor="#eeeedd"> <b>Outputs</b> </td></tr>
      <xsl:apply-templates select="output"/>
      <tr><td colspan="2" bgcolor="#eeeedd"> <b>Inputs</b> </td></tr>
      <xsl:apply-templates select="input"/>
    </table>

  </xsl:template>

  <!-- metadata about one input or output -->
  <xsl:template match="input|output">
    <xsl:variable name="param_name" select="@name"/>
    <tr bgcolor="#eae9c2">
      <td valign="top"><b><xsl:value-of select="@name"/></b></td>
      <td><table border="0" cellspacing="1" cellpadding="1" bgcolor="white" width="100%">
      <xsl:apply-templates select="@*[local-name() != 'name']" mode="as-row"/>
      <xsl:apply-templates select="allowed" mode="as-row"/>
      <xsl:apply-templates select="../analysis_extension/parameter/base[@name                       =$param_name]/*" mode="as-row"/>
      <xsl:apply-templates select="../analysis_extension/parameter/base[concat(@name,'_url')        =$param_name]/*" mode="as-row"/>
      <xsl:apply-templates select="../analysis_extension/parameter/base[concat(@name,'_direct_data')=$param_name]/*" mode="as-row"/>
      <xsl:apply-templates select="../analysis_extension/parameter/base[concat(@name,'_usa')        =$param_name]/*" mode="as-row"/>
      </table></td>
    </tr>
  </xsl:template>

  <!-- attributes and elements expressed as a (bold)name and value -->
  <xsl:template match="@*[local-name() != 'help_URL']|description|default|prompt|help" mode="as-row">
    <tr>
      <td valign="top" width="80"><em><xsl:value-of select="local-name()"/></em></td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <!-- more-values elements -->
  <xsl:template match="allowed" mode="as-row">

    <xsl:if test="position() = 1">
      <xsl:text disable-output-escaping = "yes">&lt;tr&gt;</xsl:text>
      <td valign="top" width="80"><em><xsl:value-of select="local-name()"/></em></td>
      <xsl:text disable-output-escaping = "yes">&lt;td&gt;</xsl:text>
    </xsl:if>

    <xsl:value-of select="."/>
    <xsl:if test="position() != last()">
      <xsl:text>, </xsl:text>
    </xsl:if>

    <xsl:if test="position() = last()">
      <xsl:text disable-output-escaping = "yes">&lt;/td&gt;</xsl:text>
      <xsl:text disable-output-escaping = "yes">&lt;/tr&gt;</xsl:text>
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>
